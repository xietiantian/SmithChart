package transmission;

/*
 *        文件名:Complex.java
 *        作者    :yangdk
 *        E-mail:jidacun@163.com
 *        主页    :http://blog.csdn.net/yang_dk
 */
public class Complex {
	double real, img; // 实部和虚部

	public Complex() // 默认构造方法
	{
		this.real = 0;
		this.img = 0;
	}

	public Complex(double real, double img) // 带参数的构造方法
	{
		this.real = real;
		this.img = img;
	}

	public Complex(Complex a) // 拷贝构造方法
	{
		this.real = a.real;
		this.img = a.img;
	}

	public void clone(Complex a) // 拷贝方法
	{
		this.real = a.real;
		this.img = a.img;
	}

	public Complex phaseModify(double dp) {// 修正相位：例如a.phaseModify(dp)返回a*e^(j*dp)
		Complex temp1 = new Complex();
		Complex temp2 = new Complex(Math.cos(dp), Math.sin(dp));
		temp1 = temp1.mulComplex(temp2, this);
		return temp1;
	}

	public double getReal() {
		return this.real;
	} // 得到实部

	public double getImage() {
		return this.img;
	} // 得到虚部

	public double getReal(Complex c) {
		return c.real;
	} // 得到复数c的实部，这两个函数看起来好像有点多余，但在特殊的情况下会有用

	public double getImage(Complex c) {
		return c.img;
	} // 得到复数c的虚部

	public void setReal(double real) {
		this.real = real;
	} // 设置实部

	public void setImage(double img) {
		this.img = img;
	} // 设置虚部

	public double getmodulus() { // 返回模值
		double temp = Math.pow(this.real, 2) + Math.pow(this.img, 2);
		temp = Math.sqrt(temp);
		return temp;
	}

	public double getphase() { // 返回相位
		double temp = Math.atan2( this.img,this.real);
		return temp;
	}

	public Complex addComplex(Complex a, Complex b) // 两个复数相加，结果返回
	{
		Complex temp = new Complex();
		temp.real = a.real + b.real;
		temp.img = a.img + b.img;
		return temp;
	}

	public Complex addReal(Complex a, double b) // 一个复数和一个实数相加，结果返回
	{
		Complex temp = new Complex();
		temp.real = a.real + b;
		temp.img = a.img;
		return temp;
	}

	public Complex decReal(Complex a, double b) // 一个复数和一个实数相减，结果返回
	{
		Complex temp = new Complex();
		temp.real = a.real - b;
		temp.img = a.img;
		return temp;
	}

	public Complex mulReal(Complex a, double b) {// 乘上一个实数
		Complex temp = new Complex();
		temp.real = a.real * b;
		temp.img = a.img * b;
		return temp;
	}

	public Complex decComplex(Complex a, Complex b) // 两个复数相减，结果返回
	{
		Complex temp = new Complex();
		temp.real = a.real - b.real;
		temp.img = a.img - b.img;
		return temp;
	}

	public Complex mulComplex(Complex a, Complex b) // 两个复数相乘，结果返回
	{
		Complex temp = new Complex();
		temp.real = a.real * b.real - a.img * b.img;
		temp.img = a.real * b.img + a.img * b.real;
		return temp;
	}

	public Complex divComplex(Complex a, Complex b) // 两个复数相除，结果返回
	{
		Complex temp = new Complex();
		temp.real = (a.real * b.real + a.img * b.img)
				/ (b.real * b.real + b.img * b.img);
		temp.img = (a.img * b.real - a.real * b.img)
				/ (b.real * b.real + b.img * b.img);
		return temp;
	}

	public void addComplex(Complex cplx) // 加上一个复数
	{
		this.real = this.real + cplx.real;
		this.img = this.img + cplx.img;
	}

	public void decComplex(Complex cplx) // 减去一个复数
	{
		this.real = this.real - cplx.real;
		this.img = this.img - cplx.img;
	}

	public void mulComplex(Complex cplx) // 乘与一个复数
	{
		double temp = this.real; // 下一行代码会改变this.real的值，先用一个临时变量存起来
		this.real = this.real * cplx.real - this.img * cplx.img;
		this.img = temp * cplx.img + this.img * cplx.real;
	}

	public void divComplex(Complex cplx) // 除去一个复数
	{
		double temp = this.real; // 下一行代码会改变this.real的值，先用一个临时变量存起来
		this.real = (this.real * cplx.real + this.img * cplx.img)
				/ (cplx.real * cplx.real + cplx.img * cplx.img);
		this.img = (this.img * cplx.real - temp * cplx.img)
				/ (cplx.real * cplx.real + cplx.img * cplx.img);
	}

	public String toString() {
		if (this.img > 0) {
			return String.format("%.3f+j%.3f", this.real, this.img);
		} else if (this.img < 0) {
			return String.format("%.3f-j%.3f", this.real, 0-this.img);
		} else {
			return String.format("%.3f", this.real);
		}
	}
	/**** 以上是这个复数类的所有函数，下面是一些测试的代码 ****/

//	 public void printComplex() //在console端输出这个复数,测试用
//	 {
//	 System.out.println(this.toString());//这里可以填加一点代码以判断虚部的正负，这个工作我没有做
//	 }
	
//
//	 public static void main(String[] args) //测试代码
//	 {
//	 Complex cc=new Complex(4,8);
//
//	 Complex dd=new Complex(2,2);
//	 System.out.println("phase="+dd.getphase()+"");
//	 System.out.println("phase="+dd.getmodulus()+"");
//
//	 dd=dd.phaseModify(1.57);
//	 System.out.println("phase="+dd.getphase()+"");
//	 System.out.println("phase="+dd.getmodulus()+"");
//	 System.out.println("-----------------");
//	 Complex ff=new Complex();
//	 ff=ff.addComplex(cc,dd);
//	 ff.printComplex();
//	 ff=ff.decComplex(cc,dd);
//	 ff.printComplex();
//	 ff=ff.mulComplex(cc,dd);
//	 ff.printComplex();
//	 ff=ff.divComplex(cc,dd);
//	 ff.printComplex();
//	 System.out.println("-----------------");
//	 cc.addComplex(dd);
//	 cc.printComplex();
//	 cc=new Complex(4,8);
//	 cc.decComplex(dd);
//	 cc.printComplex();
//	 cc=new Complex(4,8);
//	 cc.mulComplex(dd);
//	 cc.printComplex();
//	 cc=new Complex(4,8);
//	 cc.divComplex(dd);
//	 cc.printComplex();
//	 System.out.println("-----------------");
//	 cc.setReal(123);
//	 cc.setImage(456);
//	 cc.printComplex();
//	 System.out.println(""+cc.getReal()+"+"+cc.getImage()+"i");
//	 System.out.println("-----------------");
//	 }
}