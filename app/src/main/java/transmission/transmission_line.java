package transmission;

public class transmission_line {// 可能还需要一些get函数
	private Complex ZL = new Complex();// ZL
	private double Z0;
	private double d;// 观察点到负载的距离
	private Complex Zin = new Complex();
	private Complex ReflectionL = new Complex();// 负载处反射系数
	private double ReflectionL_mo, ReflectionL_ph;// 负载处反射系数的模值和相位
	private Complex ReflectionIn = new Complex();// 观察点处反射系数的实部和虚部
	private double ReflectionIn_mo, ReflectionIn_ph;// 观察点处反射系数的模值和相位
	private double VSWR;// 电压驻波比

	// private SmithChart SChart=new SmithChart();

	public transmission_line() {// 无参构造方法
		ReflectionL_mo = 10;// 这三个系数均取不可能的取值，表示初始化状态
		ReflectionIn_mo = 10;
		VSWR = 0;
		Z0 = 50;// Z0的默认值
		d = 0;// d的默认值
		System.out.println("init without paramater finish");
	}

//	public transmission_line(double p_RL, double p_XL, double p_Z0) {// 带参构造方法
//																		// 没有d输入
//		ZL.setReal(p_RL);
//		ZL.setImage(p_XL);
//		Z0 = p_Z0;
//		d = 0;
//		ReflectionL_mo = 10;// 表示初始化状态，反射系数等未算出
//		ReflectionIn_mo = 10;
//		VSWR = 0;
//		System.out.println("init with paramater finish");
//	}
//
//	public transmission_line(double p_ReL_re, double p_ReL_im) {// 带参构造方法 输入反射系数
//		ReflectionL.setReal(p_ReL_re);
//		ReflectionL.setImage(p_ReL_im);
//		Z0 = 50;
//		d = 0;
//		ReflectionIn_mo = 10;// 表示初始化状态，观察点处反射系数等未算出
//		VSWR = 0;
//		System.out.println("init with paramater finish");
//	}
	public double getd(){
		return d;
	}
	public Complex getZL(){
		return ZL;
	}
	public Complex getZin(){
		return Zin;
	}
	public String RefLtoString(){
		return String.format("%f*e^%f", this.ReflectionIn_mo, this.ReflectionIn_ph);
	}
	public String RefIntoString(){
		return String.format("%f*e^%f", this.ReflectionL_mo, this.ReflectionL_ph);
	}
	public double getVSWR(){
		return VSWR;
	}
	public void setAll(double p_RL, double p_XL, double p_Z0, double p_d) {
		ZL.setReal(p_RL);
		ZL.setImage(p_XL);
		Z0 = p_Z0;
		d = p_d;
		ReflectionL_mo = 10;// 表示初始化状态，反射系数等未算出
		ReflectionIn_mo = 10;
		VSWR = 0;
		System.out.println("init with paramater finish");
	}

	public void setZL(double p_RL, double p_XL) {
		ZL.setReal(p_RL);
		ZL.setImage(p_XL);

		ReflectionL_mo = 10;// 表示初始化状态，反射系数等未算出
		ReflectionIn_mo = 10;
		VSWR = 0;
		System.out.println("init ZL");
	}

	public void setZ0(double p_Z0) {// 待修改！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		Z0 = p_Z0;
		ReflectionL_mo = 10;// 这三个系数均取不可能的取值，表示初始化状态
		ReflectionIn_mo = 10;
		VSWR = 0;
	}

	public void setd(double p_d) {// 修改d （认为 ZLZ0不变）并重新计算观察点反射系数和输入阻抗
		d = p_d;
		ReflectionIn_mo = 10;
	}

	public Complex ReLCalcReIn() {// 计算ReflectionIn
		if (d == 0) {
			ReflectionIn.clone(ReflectionL);
		} else if (d > 0) {
			if (ReflectionL_mo > 1) {// 没有算出ReL，就先算ReL
				this.ZLZ0CalcReL();
			}
			ReflectionIn = ReflectionL.phaseModify(-4 * Math.PI * d);
			ReflectionIn_mo = ReflectionIn.getmodulus();
			ReflectionIn_ph = ReflectionIn.getphase();
		} else {
			System.out.println("paramater l/lamda should be positive");
		}
		return ReflectionIn;
	}

	public Complex ReInCalcReL() {// 计算ReflectionL
		if (d == 0) {
			ReflectionL.clone(ReflectionIn);
			ReflectionL_mo = ReflectionL.getmodulus();
			ReflectionL_ph = ReflectionL.getphase();
		} else if (d > 0) {
			if (ReflectionIn_mo < 1) {// 要求ReflectionIn已知
				ReflectionL = ReflectionIn.phaseModify(4 * Math.PI * d);
				ReflectionL_mo = ReflectionL.getmodulus();
				ReflectionL_ph = ReflectionL.getphase();
			}
		} else {
			System.out.println("paramater l/lamda should be positive");
		}
		return ReflectionL;
	}

	public Complex ZLZ0CalcReL() { // 计算负载处反射系数
		Complex temp = new Complex();
		ReflectionL = ReflectionL.decReal(ZL, Z0);
		temp = temp.addReal(ZL, Z0);
		ReflectionL.divComplex(temp);
		ReflectionL_mo = ReflectionL.getmodulus();
		ReflectionL_ph = ReflectionL.getphase();
		return ReflectionL;
	}

	public Complex ReInCalcZin() {// 计算输入阻抗
		 if (d >= 0) {
			Complex temp1 = new Complex();
			Complex temp2 = new Complex();
			temp1 = temp1.addReal(ReflectionIn, 1);// temp1=ReIn+1
			temp2 = temp2.decReal(ReflectionIn, 1);// temp1=ReIn-1
			temp1.divComplex(temp2);// temp1=temp1/temp2
			temp1 = temp1.mulReal(temp1, Z0);// temp1=temp1*Z0
			Zin = temp1.phaseModify(Math.PI);// temp1=-temp1
		} else {
			System.out.println("paramater l/lamda should be positive");
		}
		return Zin;
	}

	public Complex ReLCalcZL() {// 计算负载阻抗
		Complex temp1 = new Complex();
		Complex temp2 = new Complex();
		temp1 = temp1.addReal(ReflectionL, 1);// temp1=ReL+1
		temp2 = temp2.decReal(ReflectionL, 1);// temp1=ReL-1
		temp1.divComplex(temp2);// temp1=temp1/temp2
		temp1 = temp1.mulReal(temp1, Z0);// temp1=temp1*Z0
		ZL = temp1.phaseModify(Math.PI);// temp1=-temp1
		return ZL;
	}

	public double ReLmoCalcVSWR() {
		if (ReflectionL_mo > 1) {// 没有算出ReL，就先算ReL
			this.ZLZ0CalcReL();
		}
		VSWR = (1 + ReflectionL_mo) / (1 - ReflectionL_mo);
		return VSWR;
	}

	public Complex abCalcReIn(double aa, double bb) {// ab Z0 d已知
		ReflectionIn.setReal(aa);
		ReflectionIn.setImage(bb);
		ReflectionIn_mo = ReflectionIn.getmodulus();
		ReflectionIn_ph = ReflectionIn.getphase();
		return ReflectionIn;
	}
	public void dZLZ0CalcAll() {// ZL d Z0 d已知
		ZLZ0CalcReL();
		ReLCalcReIn();
		ReInCalcZin();
		ReLmoCalcVSWR();
	}
	
	// public static void main(String[] args) { // 测试代码
	// transmission_line tt = new transmission_line(100, -200, 100, 0.83333);
	// tt.ZLZ0CalcReL();
	// tt.ReLCalcReIn();
	// tt.ReInCalcZin();
	// tt.ReLmoCalcVSWR();
	// System.out.println("ReflectionL_mo=" + tt.ReflectionL_mo);
	// System.out.println("ReflectionL_ph=" + tt.ReflectionL_ph);
	// System.out.println("ReflectionIn_mo=" + tt.ReflectionIn_mo);
	// System.out.println("ReflectionIn_ph=" + tt.ReflectionIn_ph);
	// System.out.print("Zin=");
	// tt.Zin.printComplex();
	// System.out.println("VSWR=" + tt.VSWR);
	// }
}
