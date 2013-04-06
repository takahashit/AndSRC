package jp.co.entity.sosya;

import java.util.Date;

public class GPSRecord {

    private Long id;

    // ユーザーID
    private long userid;
    // 緯度
    private double latitude;
    // 経度
    private double longitude;
    // データ作成日時
    private Date createdAt;

    // ユーザID取得
    public long getUserid() {
        return userid;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
