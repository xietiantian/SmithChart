package transmission;

public class Circle {    

    private double radius;
    private double X;//圆心坐标x
    private double Y;//圆心坐标y
    
    public Circle(){
    	radius=0;
    	X=0;
    	Y=0;
    }
    
    public Circle(double r,double x,double y){
        this.radius = r;
    	this.X=x;
    	this.Y=y;
    }
    
    public void setRadius(double r) {
        this.radius = r;
    }
    public double getRadius() {
        return radius;
    }
    
    public void setX(double x) {
        this.X = x;
    }
    public double getX() {
        return X;
    }
    
    public void setY(double y) {
        this.Y= y;
    }
    public double getY() {
        return Y;
    }
    
    public String toString(){
        return String.format("半径为%f，圆心为（%f，%f）", this.radius,this.X,this.Y);
    }
}