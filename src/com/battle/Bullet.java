package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet {
	private int bulletX;            //ը������
	private int bulletY;
	private Bitmap bulletBitmap;    //ը��ͼ 
	private int bulletSpeed=15;     //ը���ƶ��ٶ�
	
	Bullet(Bitmap bulletBitmap,int bulletX,int bulletY){
		this.bulletBitmap=bulletBitmap;
		this.bulletX=bulletX;
		this.bulletY=bulletY;
	}
	
	//����ը��
	public void drawBullet(Canvas canvas){
		canvas.drawBitmap(bulletBitmap, bulletX, bulletY, null);
	}
	
	//�ƶ�ը��
	public void moveBullet(){
		bulletY-=bulletSpeed;
	}

	//��ȡը��������
	public int getBulletX(){
		return bulletX;
	}

	//��ȡը��������
	public int getBulletY(){
		return bulletY;
	}
}
