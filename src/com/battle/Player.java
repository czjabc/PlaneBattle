package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//��ҷɻ���
public class Player {
	private Bitmap playerBitmap;    //�ɻ�ͼ
	
	Player(Bitmap bitmap){this.playerBitmap=bitmap;}
	
	//������ҷɻ�
	public void drawPlayer(Canvas canvas,int playerX,int playerY){
		canvas.drawBitmap(playerBitmap, playerX, playerY, null);
	}
	
}
