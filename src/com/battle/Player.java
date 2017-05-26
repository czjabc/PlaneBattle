package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//玩家飞机类
public class Player {
    private Bitmap playerBitmap;    //飞机图
    
    Player(Bitmap bitmap){this.playerBitmap=bitmap;}
    
    //绘制玩家飞机
    public void drawPlayer(Canvas canvas,int playerX,int playerY){
        canvas.drawBitmap(playerBitmap, playerX, playerY, null);
    }
    
}
