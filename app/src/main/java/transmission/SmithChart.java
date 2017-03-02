package transmission;

public class SmithChart {

	private Circle ReflectCircle = new Circle();
	private Circle VSWRCircle = new Circle();
	private Circle rCircle = new Circle();
	private Circle xCircle = new Circle();
	private double a;// 圆图上一点的横坐标(归一化的)
	private double b;// 圆图上一点的纵坐标
	private int Flag;
	// constant
	public final int Normal = 0;// 除了下面两个外的其他正常情况
	public final int OC = 1;// OC:b=0,a=1
	public final int b0 = 2;// b=0,a!=1
	public final int NotIn = 4;

	public SmithChart() {
		a = 10;
		b = 10;
		Flag = NotIn;
	}

	
	public int getFlag() {
		return Flag;
	}

	public void setByR(Complex RIn, double p_Z0) {
		a = RIn.getReal() / p_Z0;
		b = RIn.getImage() / p_Z0;
		this.setFlag();
//		this.Calc();
	}

	public void setab(double aa, double bb) {
		a = aa;
		b = bb;
		this.setFlag();
	}

	public void setFlag() {
		if (a * a + b * b <= 1) {
			if (a != 1 && b != 0) {
				Flag = Normal;
			} else if (b == 0 && a != 1) {
				Flag = b0;
			} else {
				Flag = OC;
			}
		} else {
			Flag = NotIn;
		}
	}

	public Circle ReflectCircleCalc() {// 得到等反射系数圆半径

		double temp = Math.sqrt(a * a + b * b);
		if (Flag!=NotIn) {
			ReflectCircle.setRadius(temp);
			return ReflectCircle;
		} else {
			return null;
		}
	}

	public Circle VSWRCircleCalc() {// 得到等驻波比圆

		double temp = (1-ReflectCircle.getRadius())/2;
		if (Flag!=NotIn) {
			VSWRCircle.setRadius(temp);
			VSWRCircle.setX(1-temp);
			VSWRCircle.setY(0);
			return VSWRCircle;
		} else {
			return null;
		}
	}
	
	public Circle rCircleCalc() {// 电阻圆
		if(Flag!=OC && Flag!=NotIn){
			double temp;
			temp = (b * b / (1 - a) + (1 - a)) / 2;
			rCircle.setRadius(temp);
			rCircle.setX(1 - temp);
			rCircle.setY(0);
			return rCircle;
		}
		else{
			return null;
		}
	}

	public Circle xCircleCalc() {// 电抗圆
		if (Flag==Normal){
			double temp;
			temp = (1 - a) * this.rCircle.getRadius() / b;
			xCircle.setY(temp);
			xCircle.setX(1);
			xCircle.setRadius(Math.abs(temp));
			return xCircle;
		}
		else{
			return null;
		}

	}
}
