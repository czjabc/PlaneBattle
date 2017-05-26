package com.battle;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//�л���
public class Enemy {
	private int enemyX;          //�л�����
	private int enemyY;
	private Bitmap enemyBitmap;  //�л�ͼ
	private int enemySpeed;      //�л��ƶ��ٶ�
	private int blood;           //�л�Ѫ��
	private int type;            //�л����࣬Ŀǰ��С�͡����͡�����3��
	private int score;           //����л����õķ���
	
	Enemy(Bitmap enemyBitmap,int enemyX,int enemyY,int type){
		this.enemyBitmap=enemyBitmap;
		this.enemyX=enemyX;
		this.enemyY=enemyY;
		this.type=type;
		Random r=new Random();
		switch(type){
		case 0:
			blood=1;
			enemySpeed=r.nextInt(3)+5;
			score=100;
			break;
		case 1:
			blood=3;
			enemySpeed=r.nextInt(3)+3;
			score=500;
			break;
		case 2:
			blood=10;
			enemySpeed=3;
			score=1500;
			break;
		}
	}
	
	//���Ƶл�
	public void drawEnemy(Canvas canvas){
		canvas.drawBitmap(enemyBitmap, enemyX, enemyY, null);
	}
	
	//�ƶ��л�
	public void moveEnemy(){
		enemyY+=enemySpeed;
	}
	
	//���ел�
	public boolean hitEnemy(){
		if(--blood<=0)return true;
		return false;
	}
	
	//��ȡ�л�����
	public int getType(){
		return type;
	}
	
	//��ȡ�л�������
	public int getEnemyX(){
		return enemyX;
	}

	//��ȡ�л�������
	public int getEnemyY(){
		return enemyY;
	}
	
	//��ȡ�л��ƶ��ٶ�
	public int getEnemySpeed(){
		return enemySpeed;
	}
	
	//��ȡ�л�ͼ��
	public int getEnemyBimmapWidth(){
		return enemyBitmap.getWidth();
	}
	
	//��ȡ�л�ͼ��
	public int getEnemyBitmaoHeight(){
		return enemyBitmap.getHeight();
	}
	
	//��ȡ����л����÷���
	public int getScore(){
		return score;
	}
	
}

