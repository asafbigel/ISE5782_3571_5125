package renderer;
import primitives.*;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;


public class Camera {
    /**
     * parameters are:
     * Point p0- represent the camera position in model.
     * Vector vto- perpendicular to both camera and view plane.
     * Vector vup- perpendicular to vto.
     * Vector vright- perpendicular to both vup and vto.
     * double height- height of view plane.
     * double width- width of view plane.
     * double distance- of view plane from camera.
     * ImageWriter iw- used to write to the picture pixels.
     * RayTracerBase rtb- used to calculate the intersection point color.
     */
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;
    private double height;
    private double width;
    private double distance;
    private ImageWriter iw;
    private RayTracerBase rtb;

    /**
     * getter for Point p0.
     *
     * @return value of p0.
     */
    public Point getP0() {
        return p0;
    }

    /**
     * getter for Vto.
     *
     * @return Vto value (as Vector instance).
     */
    public Vector getVTo() {
        return vTo;
    }

    /**
     * getter for Vup.
     *
     * @return Vup value (as Vector instance).
     */
    public Vector getVUp() {
        return vUp;
    }

    /**
     * getter for Vright.
     *
     * @return Vright value (as Vector intance).
     */
    public Vector getVRight() {
        return vRight;
    }

    /**
     * getter for height field, representing the view plane height.
     *
     * @return Height of view plane (double).
     */
    public double getHeight() {
        return height;
    }

    /**
     * getter for Width, representing Width of veiw plane.
     *
     * @return Weidth of view plane (double).
     */
    public double getWidth() {
        return width;
    }

    /**
     * getter for Distance field, representing the distance of the view plane from the camera.
     *
     * @return Distance of view plane from camera (double).
     */
    public double getDistance() {
        return distance;
    }

    /**
     * ctor for camera.
     * @param location Point type, rpreseting the camera location in space.
     * @param vTo      Vector type, representing the camera direction of viewing.
     * @param vUp
     */
    public Camera(Point location, Vector vTo, Vector vUp) {
        if (vTo.dotProduct(vUp) != 0)
            throw new IllegalArgumentException();
        //#####we should catch this expception some where!!!!!#########
        this.p0 = location;
        this.vTo = vTo.normalize();
        this.vUp = vUp.normalize();
        //should it be toxup or upxto?
        this.vRight = this.vTo.crossProduct(this.vUp);
        iw = null;
        rtb = null;
    }

    public Camera(Point cameraLocation,Point imageCenterLocation) {
        this.p0 = cameraLocation;
        this.vTo = imageCenterLocation.subtract(cameraLocation).normalize();
        double x = vTo.getX();
        double y = vTo.getY();
        double z = vTo.getZ();
        this.vUp = new Vector(x,y, -(x*x + y*y)/z).normalize();
        //if (vTo.dotProduct(vUp) != 0)
         //   throw new IllegalArgumentException();
        this.vRight = this.vTo.crossProduct(this.vUp);
        iw = null;
        rtb = null;
    }

    /**
     * setter for view plane size.
     * notice that it returns *this* instance for cat.
     * @param width of view plane.
     * @param height of view plane.
     * @return this camera so we can call another function if we wish to.
     */
    public Camera setVPSize(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * setter for view plane distance from camera.
     * @param distance view plane distance from camera.
     * @return this camera so we could call another function if we wish to.
     */
    public Camera setVPDistance(double distance) {
        this.distance = distance;
        return this;
    }

    /**
     * a seter for ImageWriter param, who hold the resolution of the picture.
     * @param iw  holds nX and nY parameters.
     * @return this camera so we could call another function if we wish to.
     */
    public Camera setImageWriter(ImageWriter iw) {
        this.iw = iw;
        return this;
    }

    /**
     * setter for RayTracerBase param, who hold the scene param, witch holds the name of the scene,
     * the geometrics list, background color,and ambient light.
     * @param rtb RayTracer type, hold scene type.
     * @return this camera so we could call another function if we wish to.
     */
    public Camera setRayTracer(RayTracerBase rtb) {
        this.rtb = rtb;
        return this;
    }

    /**
     * a function used to construct each ray from the camera to the view plane,
     * for each pixel we determine.
     * @param nX resolution in terms of x.
     * @param nY resolution in terms of y.
     * @param j length value of current pixel in view plane.
     * @param i width value of current pixel in view plane.
     * @return the ray from camera point to the exact pixel.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        //image center.
        Point pc = p0.add(vTo.scale(distance));
        //ratio.
        double Ry = alignZero(height / nY);
        double Rx = alignZero(width / nX);
        //pixel[i,j].
        double Xj = alignZero(Rx * (j - (nX - 1d) / 2d));
        double Yi = alignZero(-Ry * (i - (nY - 1d) / 2d));
        //Point Pij=pc.add(vRight.scale(Xj).add(vUp.scale(Yi)));
        Point Pij = pc;
        if (!isZero(Xj))
            Pij = Pij.add(vRight.scale(Xj));
        if (!isZero(Yi))
            Pij = Pij.add(vUp.scale(Yi));
        return new Ray(p0, Pij.subtract(p0));
        //return null;
    }

    /**
     * uses  Image Writer parameter to color the picture pixel by the color returned
     * from RAyTracer param.
     * this function does not return a value.
     */
    public Camera renderImage() {
        if (height == 0 || width == 0 || iw == null || rtb == null) {
            throw new MissingResourceException("not enough variables.", "Camera", "1");
        }
        for (int j = 0; j < iw.getNx(); j++)
            for (int i = 0; i < iw.getNy(); i++) {
                Color c = castRay(j,i);
                iw.writePixel(i, j, c);
            }
        return this;
    }

    public Camera renderImageWithThread() {
        if (height == 0 || width == 0 || iw == null || rtb == null) {
            throw new MissingResourceException("not enough variables.", "Camera", "1");
        }
        int nY = iw.getNy();
        int nX = iw.getNx();
        Pixel.initialize(nY, nX, 1);
        int threadsCount = 3;
        while (threadsCount-- > 0) {
            new Thread(() -> {
                for (Pixel pixel = new Pixel(); pixel.nextPixel(); Pixel.pixelDone()) {
                    Color c = castRay(pixel.col, pixel.row);
                    iw.writePixel(pixel.row, pixel.col, c);
                }
            }).start();
        }
        Pixel.waitToFinish();
        return this;
    }

    /**
     * function get a tow width and length value of view plane pixel, and calculate its color.
     * it uses the constructRay function.
     * @param i width value of pixel.
     * @param j length value of pixel.
     * @return pixel color.
     */
    private Color castRay(int i, int j) {
        return rtb.traceRay(constructRay(iw.getNx(), iw.getNy(), j, i));
    }

    /**
     * function only used to print a grid on picture.
     * @param interval the space between grid lines.
     * @param color what color grid lines are.
     */
    public void printGrid(int interval, Color color) {
        //check if iw is not empty first.
        if (iw == null)
            throw new MissingResourceException("no image writer value", "Camera", "2");
        for (int j = 0; j < iw.getNx(); j += interval) {
            for (int i = 0; i < iw.getNy(); i++) {
                iw.writePixel(j, i, color);
            }
        }
        for (int i = 0; i < iw.getNx(); i += interval) {
            for (int j = 0; j < iw.getNy(); j++) {
                iw.writePixel(j, i, color);
            }
        }
    }

    /**
     * function just create an image object, simply by calling iw writeToImage function.
     */
    public void writeToImage() {
        //check if iw is not empty first.
        if (iw == null)
            throw new MissingResourceException("no image writer value", "Camera", "3");
        iw.writeToImage();
    }
    public void writeToImage(String path) {
        //check if iw is not empty first.
        if (iw == null)
            throw new MissingResourceException("no image writer value", "Camera", "3");
        iw.writeToImage(path);
    }

    public  Camera moveRight(double dis){
        if (dis != 0)
            p0 = p0.add(vRight.scale(dis));
        return this;
    }
    public  Camera moveForward(double dis){
        if (dis != 0)
            p0 = p0.add(vTo.scale(dis));
        return this;
    }
    public  Camera moveUp(double dis){
        if (dis != 0)
            p0 = p0.add(vUp.scale(dis));
        return this;
    }

    /**
     * Routing camera to the left
     * @param theta the angle
     * @return The new camera
     */
    public Camera rotationLeft(double theta){
        vTo = vTo.Roatate(theta, vUp);
        //vUp = vUp.Roatate(theta,vUp);
        vRight = vRight.Roatate(theta,vUp);
        return this;
    }

    /**
     * Routing camera to up
     * @param theta the angle
     * @return The new camera
     */
    public Camera rotationUp(double theta){
        vTo = vTo.Roatate(theta, vRight);
        vUp = vUp.Roatate(theta,vRight);
        //vRight = vRight.Roatate(theta, vRight);
        return this;
    }

    /**
     * Routing camera to the side
     * @param theta the angle
     * @return The new camera
     */
    public Camera rotationOnSide(double theta){
//        vTo = vTo.Roatate(theta, vTo);
        vUp = vUp.Roatate(theta,vTo);
        vRight = vRight.Roatate(theta, vTo);
        return this;
    }

    private Vector rotationL(Vector v, double theta) {
        double x =  v.getX();
        double y = v.getY();
        double x1 = x*Math.cos(theta)- y* Math.sin(theta);
        double y1 = x*Math.sin(theta)+ y* Math.cos(theta);
        double z1 = v.getZ();
        return new Vector(x1,y1,z1);
    }

    private Vector rotationU(Vector v, double theta) {
        double y = v.getY();
        double z = v.getZ();
        double x1 = v.getX();
        double y1 = y*Math.cos(theta)- z* Math.sin(theta);
        double z1 = y*Math.sin(theta)+ z* Math.cos(theta);
        return new Vector(x1,y1,z1);

    }

    private Vector rotation(Vector v, Vector dir, double theta) {
        theta = (theta/360)*2*Math.PI;    // from regular to radian
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        Vector v1 =v.scale(cos);
        Vector v2 =dir.crossProduct(v).scale(sin);
        Vector v3 =dir.scale(dir.dotProduct(v)).scale(1-cos);
        return  v1.add(v2).add(v3);
    }
}
