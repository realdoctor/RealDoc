package com.real.doctor.realdoc.util;
import com.real.doctor.realdoc.model.VideoListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 2018/4/19.
 */

public class DataUtils {

    public static final String VIDEO_URL_01 = "http://jiajunhui.cn/video/kaipao.mp4";
    public static final String VIDEO_URL_02 = "http://jiajunhui.cn/video/kongchengji.mp4";
    public static final String VIDEO_URL_03 = "http://jiajunhui.cn/video/allsharestar.mp4";
    public static final String VIDEO_URL_04 = "http://jiajunhui.cn/video/edwin_rolling_in_the_deep.flv";
    public static final String VIDEO_URL_05 = "http://jiajunhui.cn/video/crystalliz.flv";
    public static final String VIDEO_URL_06 = "http://jiajunhui.cn/video/big_buck_bunny.mp4";
    public static final String VIDEO_URL_07 = "http://jiajunhui.cn/video/trailer.mp4";

    public static String[] urls = new String[]{
            VIDEO_URL_01,
            VIDEO_URL_02,
            VIDEO_URL_03,
            VIDEO_URL_04,
            VIDEO_URL_05,
            VIDEO_URL_06,
            VIDEO_URL_07,
    };

    public static List<VideoListBean> getVideoList() {
        List<VideoListBean> videoList = new ArrayList<>();
        videoList.add(new VideoListBean(
                "你欠缺的也许并不是能力",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/b/a/c36e048e284c459686133e66a79e2eba.jpg",
                "http://47.98.156.204/static/files/video/20180712/20180712151513_yyqNtKeVm4.mp4"));

        videoList.add(new VideoListBean(
                "坚持与放弃",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/0/4/e4c8836bfe154d76a808da38d0733304.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2015/08/27/SB13F5AGJ_sd.mp4"));

        videoList.add(new VideoListBean(
                "不想从被子里出来",
                "http://open-image.nosdn.127.net/57baaaeaad4e4fda8bdaceafdb9d45c2.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4"));

        videoList.add(new VideoListBean(
                "不耐烦的中国人?",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/9/ac655948c705413b8a63a7aaefd4cde9.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/05/31/SCKR8V6E9_hd.mp4"));

        videoList.add(new VideoListBean(
                "神奇的珊瑚",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/4/75bc6c5227314e63bbfd5d9f0c5c28e4.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));

        videoList.add(new VideoListBean(
                "怎样经营你的人脉",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2018/3/b/c/9d451a2da3cf42b0a049ba3e249222bc.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/04/19/SDEQS1GO6_hd.mp4"));

        videoList.add(new VideoListBean(
                "怎么才能不畏将来",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2018/1/c/8/1aec3637270f465faae52713a7c191c8.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/25/SD82Q0AQE_hd.mp4"));

        videoList.add(new VideoListBean(
                "音乐和艺术如何改变世界",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2017/12/2/8/f30dd5f2f09c405c98e7eb6c06c89928.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4"));

        return videoList;
    }


}
