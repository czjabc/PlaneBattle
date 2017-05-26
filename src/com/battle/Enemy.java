package com.battle;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//敌机类
public class Enemy {
	private int enemyX;          //敌机坐标
	private int enemyY;
	private Bitmap enemyBitmap;  //敌机图
	private int enemySpeed;      //敌机移动速度
	private int blood;           //敌机血量
	private int type;            //敌机种类，目前有小型、中型、大型3种
	private int score;           //击落敌机所得的分数
	
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
	
	//绘制敌机
	public void drawEnemy(Canvas canvas){
		canvas.drawBitmap(enemyBitmap, enemyX, enemyY, null);
	}
	
	//移动敌机
	public void moveEnemy(){
		enemyY+=enemySpeed;
	}
	
	//击中敌机
	public boolean hitEnemy(){
		if(--blood<=0)return true;
		return false;
	}
	
	//获取敌机种类
	public int getType(){
		return type;
	}
	
	//获取敌机横坐标
	public int getEnemyX(){
		return enemyX;
	}

	//获取敌机纵坐标
	public int getEnemyY(){
		return enemyY;
	}
	
	//获取敌机移动速度
	public int getEnemySpeed(){
		return enemySpeed;
	}
	
	//获取敌机图宽
	public int getEnemyBimmapWidth(){
		return enemyBitmap.getWidth();
	}
	
	//获取敌机图高
	public int getEnemyBitmaoHeight(){
		return enemyBitmap.getHeight();
	}
	
	//获取击落敌机所得分数
	public int getScore(){
		return score;
	}
	
}

