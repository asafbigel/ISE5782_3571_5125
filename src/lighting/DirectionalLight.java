package lighting;

import primitives.*;


public class DirectionalLight extends Light implements LightSource{
    private Vector direction;

    /**
     * ctor for directional light.
     * @param intensity Color type, which holds Double3 type representing rgb values.
     * @param direction Vector type, representing the light source directions (for it is a directional light).
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    /**
     * get light intensity for directional light
     * @param p an intersection point.
     * @return the light source intensity.
     */
    @Override
    public Color getIntensity(Point p) {
        return getIntensity();
        //in directional light the intensity is the same as source.
    }

    /**
     * function return this light source direction.
     * @param p what should we put here??!
     * @return light source direction vector.
     */
    @Override
    public Vector getL(Point p) {
        return this.direction;
    }

    /**
     * return distance from point to light source position. here value is constant
     * with POSITIVE INFINITY VALUE.
     * @param point that we wnat to know it distance from light source.
     * @return constant of POSITIVE INFINITY.
     */
    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }
}
