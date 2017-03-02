package transmission;

public class Convert {
	final static int sx0 = 1144;// 原点在图上的横坐标
	final static int sy0 = 1144;// 原点在图上的纵坐标
	final static int sr = 1144 - 170;// 原图圆半径

	//点的左上角坐标转化为a、b
	public static double x2a(float xx, int psize, float ratio, float tx) {
		// ratio:totalRaio即相对于原图片的放大倍数 tx、ty：totaltranslateX、Y psize：point长宽
		double aa = ((xx + psize / 2 - tx) / ratio - sx0) / sr;
		return aa;
	}

	public static double y2b(float yy, int psize, float ratio, float ty) {
		// ratio:totalRaio即相对于原图片的放大倍数 tx、ty：totaltranslateX、Y psize：point长宽
		double bb = (sy0 - (yy + psize / 2 - ty) / ratio) / sr;
		return bb;
	}
	//a、b转化为点的中心坐标
	public static float b2y(double bb, float ratio, float ty) {
		// ratio:totalRaio即相对于原图片的放大倍数 tx、ty：totaltranslateX、Y psize：point长宽
		float yy = (float) ((sy0 - bb * sr) * ratio + ty);
		return yy;
	}

	public static float a2x(double aa,  float ratio, float tx) {
		// ratio:totalRaio即相对于原图片的放大倍数 tx、ty：totaltranslateX、Y psize：point长宽
		float xx = (float)((sx0+ aa * sr) * ratio + tx);
		return xx;
	}
	
	public static Circle convertCircle(Circle cc, float ratio, float tx,float ty){
		if (cc!=null){
			Circle temp=new Circle();
			temp.setX(Convert.a2x(cc.getX(),ratio, tx));
			temp.setY(Convert.b2y(cc.getY(),ratio, ty));
			temp.setRadius(cc.getRadius()*sr*ratio);
			return temp;
		}
		else{
			return null;
		}		
	}
	
	public static boolean checkab(double aa, double bb) {
		if (aa * aa + bb * bb <= 1) {
			return true;
		} else {
			return false;
		}
	}
}