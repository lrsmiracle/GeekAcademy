package com.jikexueyuan.playsoundandlrc;

/**
 * Created by Liurs on 2016/7/1.
 **/
public class SongModel {

    private int playtime ;
    private String lyrics;

    public SongModel(String time,String str){
        String[] temp = time.split(":");
        playtime+=60*1000*Integer.parseInt(temp[0]);
        String[] temp2 ={ temp[1].substring(0,2),temp[1].substring(3,5)};
        playtime+=1000*Integer.parseInt(temp2[0])+10*Integer.parseInt(temp2[1]);
        lyrics = str;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    @Override
    public String toString() {
        return "SongModel{" +
                "lyrics='" + lyrics + '\'' +
                ", playtime=" + playtime +
                '}';
    }
}
