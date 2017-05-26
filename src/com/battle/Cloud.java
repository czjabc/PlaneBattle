package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//��(����)��
public class Cloud {
	private Bitmap cloudBitmap;    //��(����)ͼ
	private int cloudX;            //��(����)����
	private int cloudY;
	private int cloudSpeed=6;      //��(����)�ƶ��ٶ�
	
	Cloud(Bitmap cloudBitmap,int cloudX,int cloudY){
		this.cloudBitmap=cloudBitmap;
		this.cloudX=cloudX;
		this.cloudY=cloudY;
	}
	
	//������(����)
	public void drawCloud(Canvas canvas){
		canvas.drawBitmap(cloudBitmap, cloudX, cloudY, null);
	}
	
	//�ƶ���(����)
	public void moveCloud(){
		cloudY+=cloudSpeed;
	}
	
	//��ȡ��(����)������
	public int getCloudX(){
		return cloudX;
	}
	
	//��ȡ��(����)������
	public int getCloudY(){
		return cloudY;
	}
}
