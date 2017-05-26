package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet {
	private int bulletX;            //Õ¨µ¯×ø±ê
	private int bulletY;
	private Bitmap bulletBitmap;    //Õ¨µ¯Í¼ 
	private int bulletSpeed=15;     //Õ¨µ¯ÒÆ¶¯ËÙ¶È
	
	Bullet(Bitmap bulletBitmap,int bulletX,int bulletY){
		this.bulletBitmap=bulletBitmap;
		this.bulletX=bulletX;
		this.bulletY=bulletY;
	}
	
	//»æÖÆÕ¨µ¯
	public void drawBullet(Canvas canvas){
		canvas.drawBitmap(bulletBitmap, bulletX, bulletY, null);
	}
	
	//ÒÆ¶¯Õ¨µ¯
	public void moveBullet(){
		bulletY-=bulletSpeed;
	}

	//»ñÈ¡Õ¨µ¯ºá×ø±ê
	public int getBulletX(){
		return bulletX;
	}

	//»ñÈ¡Õ¨µ¯×Ý×ø±ê
	public int getBulletY(){
		return bulletY;
	}
}
