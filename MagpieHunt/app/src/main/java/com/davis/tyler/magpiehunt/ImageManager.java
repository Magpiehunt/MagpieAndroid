package com.davis.tyler.magpiehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.ImageView;

import com.ahmadrosid.svgloader.SvgDecoder;
import com.ahmadrosid.svgloader.SvgDrawableTranscoder;
import com.ahmadrosid.svgloader.SvgSoftwareLayerSetter;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caverock.androidsvg.SVG;
import com.davis.tyler.magpiehunt.Hunts.Award;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;

public class ImageManager {

    public void fillSuperBadgeImage(Context context, Hunt hunt, ImageView imageView){
        Award award = hunt.getAward();
        boolean isGrayScale = !hunt.getIsCompleted();
        if(loadImageFromFile(context, award.getSuperBadgeImageFileName(), imageView, isGrayScale)){
            return;
        }
        String url = award.getSuperBadgeIcon();
        String[] arr = url.split("\\.");
        if(arr.length >0 && arr[1].equalsIgnoreCase("svg")){
            getSVGImage(context, "http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon(), imageView);
        }
        else{
            if(hunt.getIsCompleted())
                Picasso.get().load("http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon())
                        .fit()
                        .centerCrop()
                        .into(imageView);
            else
                Picasso.get().load("http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon())
                        .transform(new GrayScaleTransformation(Picasso.get()))
                        .fit()
                        .centerCrop()
                        .into(imageView);
        }
    }

    public void fillAwardFinished(Context context, Award award, ImageView imageView){
        if(loadImageFromFile(context, award.getSuperBadgeImageFileName(), imageView, false)){
            return;
        }

        Picasso.get().load("http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon())
                .transform(new GrayScaleTransformation(Picasso.get()))
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public void fillCustomInfoWindowLandmark(Context context, Badge badge, ImageView imageView,
                                             int iconWidth, int iconHeight, Marker marker){
        if(loadImageFromFileInfoWindow(context, badge.getLandmarkImageFileName(), imageView,
                iconWidth, iconHeight, marker)){
            return;
        }
        String url = badge.getLandmarkImage();
        Picasso.get()
                .load("http://206.189.204.95/landmark/image/"+badge.getLandmarkImage())
                .resize(iconWidth, iconHeight)
                .centerCrop()
                .into(imageView, new MarkerCallback(marker));

    }
    public boolean loadImageFromFileInfoWindow(Context context, String fileName, ImageView imageView,
                                               int iconWidth, int iconHeight, Marker marker){
        File f=new File(context.getFilesDir()+"/images",fileName);
        if(f == null || !f.exists()){
            System.out.println("file doesnt exist: "+context.getFilesDir()+"/images/"+fileName);
            return false;
        }
            Picasso.get()
                    .load(f)
                    .resize(iconWidth, iconHeight)
                    .centerCrop()
                    .into(imageView, new MarkerCallback(marker));

        System.out.println("image: "+fileName+" loaded successfully");
        return true;
    }


    public void fillLandmarkImage(Context context, Badge badge, ImageView imageView){
        boolean isGrayScale = false;

        if(loadImageFromFileLandmarkInfo(context, badge.getLandmarkImageFileName(), imageView)){
            return;
        }
        String url = badge.getLandmarkImage();
        String[] arr = url.split("\\.");
        if(arr.length >0 && arr[1].equalsIgnoreCase("svg")){
            getSVGImage(context, "http://206.189.204.95/landmark/image/"+url, imageView);
        }
        else{
            Picasso.get().load("http://206.189.204.95/landmark/image/"+url).fit().centerCrop().into(imageView);
        }
    }

    //TODO try saving images to file and try downloading hunt and using this method to load imgs from filesystem.
    public void fillBadgeImage(Context context, Badge badge, ImageView imageView){
        boolean isGrayScale = !badge.getIsCompleted();
        if(loadImageFromFile(context, badge.getBadgeImageFileName(), imageView, isGrayScale)){
            return;
        }
        String url = "http://206.189.204.95/badge/icon/"+badge.getIcon();
        String[] arr = badge.getIcon().split("\\.");
        if(arr.length >0 && arr[1].equalsIgnoreCase("svg")){
            getSVGImage(context, url, imageView);
            if(!badge.getIsCompleted()){
                //setColorFilterGrayScale(imageView);
                //imageView.setColorFilter(Color.WHITE);
                //convertToGrayscale(imageView);
                //imageView.setColorFilter(context.getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
                System.out.println("setting to gray: "+imageView.getDrawable());
            }
            else{
                imageView.clearColorFilter();
            }

        }
        else{
            if(badge.getIsCompleted())
                Picasso.get().load(url).resize(200,200).into(imageView);
            else
                Picasso.get().load(url).transform(new GrayScaleTransformation(Picasso.get())).resize(200,200).into(imageView);
        }
    }


    public void getSVGImage(Context context, String url, ImageView imageView){
        GenericRequestBuilder<Uri,InputStream,SVG,PictureDrawable>
                requestBuilder = Glide.with(context)
                .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                //.placeholder(R.drawable.svg_image_view_placeholder)
                //.error(R.drawable.error_image)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        Uri uri = Uri.parse(url);
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .load(uri)
                .into(imageView);


    }

    public boolean loadImageFromFileLandmarkInfo(Context context, String fileName, ImageView imageView){
        File f=new File(context.getFilesDir()+"/images",fileName);
        if(f == null || !f.exists()){
            System.out.println("file doesnt exist: "+context.getFilesDir()+"/images/"+fileName);
            return false;
        }
            Picasso.get().load(f).fit().centerCrop().into(imageView);

        System.out.println("image: "+fileName+" loaded successfully");
        return true;
    }

    //return true if success, false if file doesn't exist;
    public boolean loadImageFromFile(Context context, String fileName, ImageView imageView, boolean isGrayScale){

        File f=new File(context.getFilesDir()+"/images",fileName);
        if(f == null || !f.exists()){
            System.out.println("file doesnt exist: "+context.getFilesDir()+"/images/"+fileName);
            return false;
        }
        if(isGrayScale){
            Picasso.get().load(f).transform(new GrayScaleTransformation(Picasso.get())).into(imageView);
        }
        else {
            Picasso.get().load(f).into(imageView);
        }
        System.out.println("image: "+fileName+" loaded successfully");
        return true;
    }

}