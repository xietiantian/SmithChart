package transmission;

public class SmithChart {
	final int sx0 = 1144;// 原点在图上的横坐标
	final int sy0 = 1144;// 原点在图上的纵坐标
	final int sr = 1144 - 170;// 原图圆半径

	private Circle ReflectCircle = new Circle();
	private Circle VSWRCircle = new Circle();
	private Circle rCircle = new Circle();
	private Circle xCircle = new Circle();
	private double a;// 圆图上一点的横坐标(归一化的)
	private double b;// 圆图上一点的纵坐标
	private float x;// 圆图上一点的横坐标(zoomimageview中点的左上角的坐标)
	private float y;// 圆图上一点的纵坐标
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

	public double geta() {
		return a;
	}

	public double getb() {
		return b;
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
	
	public boolean setByxy(float xx, float yy,int psize, float ratio, float tx, float ty){
		x=xx;
		y=yy;
		if(this.xy2ab(psize, ratio, tx, ty)){
			this.setFlag();
//			this.Calc();
			return true;
		}
		else{
			return false;
		}
	}
//
//	public void Calc() {
//		switch (Flag) {
//		case Normal:
//			this.ReflectCircleCalc();
//			this.rCircleCalc();
//			this.xCircleCalc();
//			break;
//		case b0:
//			this.ReflectCircleCalc();
//			this.rCircleCalc();
//			break;
//		case OC:
//			this.ReflectCircleCalc();
//			break;
//		}
//	}

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

	public boolean xy2ab(int psize, float ratio, float tx, float ty) {
		// ratio:totalRaio即相对于原图片的放大倍数 tx、ty：totaltranslateX、Y psize：point长宽
		float temp1 = psize / 2;
		double tempa = a;
		double tempb = b;
		a = ((x + temp1 - tx) / ratio - sx0) / sr;
		b = (sy0 - (y + temp1 - ty) / ratio) / sr;
		this.setFlag();
		if (Flag == NotIn) {// 若出界
			a = tempa;
			b = tempb;
			return false;
		} else {// 若未出界
			return true;
		}
	}

	public void ab2xy(int psize, float ratio, int tx, int ty) {
		float temp1 = psize / 2;
		x = (float) ((a * sr + sx0) * ratio + tx - temp1);
		y = (float) ((sy0 - b * sr) * ratio + ty - temp1);
	}

//	public static void main(String[] args) { // 测试代码
//		SmithChart ss = new SmithChart();
//		ss.setByab(0.5, 0.5);
//		ss.ab2xy(20, 0.471615f, 0, 0);
//		System.out.println(ss.Flag);
//		System.out.println(ss.rCircle.toString());
//		System.out.println(ss.xCircle.toString());
//		System.out.println(ss.ReflectCircle.toString());
//		System.out.println("x=" + ss.x + " y=" + ss.y);
//		ss.xy2ab(20, 0.471615f, 0, 0);
//		System.out.println("a=" + ss.a + " b=" + ss.b);
//	}
}
