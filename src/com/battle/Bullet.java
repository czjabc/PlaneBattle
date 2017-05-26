package com.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet {
    private int bulletX;            //炸弹坐标
    private int bulletY;
    private Bitmap bulletBitmap;    //炸弹图 
    private int bulletSpeed=15;     //炸弹移动速度
    
    Bullet(Bitmap bulletBitmap,int bulletX,int bulletY){
        this.bulletBitmap=bulletBitmap;
        this.bulletX=bulletX;
        this.bulletY=bulletY;
    }
    
    //绘制炸弹
    public void drawBullet(Canvas canvas){
        canvas.drawBitmap(bulletBitmap, bulletX, bulletY, null);
    }
    
    //移动炸弹
    public void moveBullet(){
        bulletY-=bulletSpeed;
    }

    //获取炸弹横坐标
    public int getBulletX(){
        return bulletX;
    }

    //获取炸弹纵坐标
    public int getBulletY(){
        return bulletY;
    }
}
