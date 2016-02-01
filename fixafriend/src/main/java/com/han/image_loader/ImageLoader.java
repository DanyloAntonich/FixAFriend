package com.han.image_loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hic on 2015-11-25.
 */
public class ImageLoader {
//    private MemoryCache memoryCache;
//    FileCache fileCache;
//    Resources resources;
//    private static final int DEFAULT_NO_OF_THREADS = 5;
//    private final int imagePlaceHolderId;
//    private Map<ImageView, String> recyclerMap = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
//    ExecutorService executorService;
//    Handler handler = new Handler();
//    private Map<String, Boolean> serverRequestMap  = Collections.synchronizedMap(new HashMap<String, Boolean>());
//
//    public ImageLoader(Context context, int imagePlaceHolderId, int lruCacheSize, int lruCacheUnit,
//                       int maxFileCacheSize, String fileCacheFolder, int numberOfThreads)
//    {
//        this.imagePlaceHolderId = imagePlaceHolderId;
//        this.resources = context.getResources();
//
//        if(numberOfThreads < 1)
//        {
//            numberOfThreads = DEFAULT_NO_OF_THREADS;
//        }
//
//        memoryCache = new MemoryCache(lruCacheSize, lruCacheUnit);
//        fileCache = new FileCache(context, maxFileCacheSize, fileCacheFolder);
//        executorService = Executors.newFixedThreadPool(numberOfThreads);
//    }
//
//    public void displayImage(String imageName, String imageUrl, ImageView imageView)
//    {
//        recyclerMap.put(imageView, imageName);
//        Bitmap bitmap = memoryCache.getImageFromCache(imageName);
//
//        if(bitmap!=null)
//        {
//            imageView.setImageBitmap(bitmap);
//        }
//        else
//        {
//            queuePhoto(imageName, imageUrl, imageView);
//
//            try
//            {
//                imageView.setImageDrawable(resources.getDrawable(imagePlaceHolderId));
//            }
//            catch(Resources.NotFoundException notFoundException)
//            {
//                throw notFoundException;
//            }
//        }
//    }
//
//    private void queuePhoto(String imageName, String imageUrl, ImageView imageView)
//    {
//        ImageInfo imageInfo = new ImageInfo(imageName, imageUrl, imageView);
//        executorService.submit(new ImageLoaderRunnable(imageInfo));
//    }
//
//    boolean imageViewReused(ImageInfo photoToLoad)
//    {
//        String imageName = recyclerMap.get(photoToLoad.getImageView());
//        if(imageName==null || !imageName.equals(photoToLoad.getImageName()))
//        {
//            return true;
//        }
//
//        return false;
//    }
}

//class ImageLoaderRunnable implements Runnable
//{
//    ImageInfo photoToLoad;
//    ImageLoaderRunnable(ImageInfo photoToLoad)
//    {
//        this.photoToLoad = photoToLoad;
//    }
//
//    public void run()
//    {
//        try
//        {
//            if(imageViewReused(photoToLoad))
//            {
//                return;
//            }
//
//            String imageName = photoToLoad.getImageName();
//            String imageUrl = photoToLoad.getImageUrl();
//            Bitmap bitmap = null;
//            Boolean isServerRequestExists = serverRequestMap.get(imageName);
//            isServerRequestExists = (isServerRequestExists == null ? false : isServerRequestExists);
//
//            if(!isServerRequestExists) //If Server request not exists, take hit
//            {
//                serverRequestMap.put(imageName, true);
//
//                try
//                {
//                    bitmap = getImage(imageName, imageUrl);
//                }
//                catch(Exception exception)
//                {
//                    serverRequestMap.put(imageName, false);
//                    if(imageViewReused(photoToLoad))
//                    {
//                        return;
//                    }
//
//                    bitmap = getImage(imageName, imageUrl);
//                    serverRequestMap.put(imageName, true);
//                }
//            }
//            else
//            {
//                return;
//            }
//
//            if(bitmap != null)
//            {
//                serverRequestMap.remove(photoToLoad.getImageName());
//                memoryCache.saveImageToCache(imageName, bitmap);
//            }
//
//            if(imageViewReused(photoToLoad))
//            {
//                return;
//            }
//
//            BitmapDisplayerRunnable bitmapDisplayer = new BitmapDisplayerRunnable(bitmap, photoToLoad);
//
//            handler.post(bitmapDisplayer);
//        }
//        catch(Throwable throwable)
//        {
//            throwable.printStackTrace();
//        }
//    }
//
//    public Bitmap getImage(String imageName, String url) throws ClientProtocolException, FileNotFoundException, InnovationMException, IOException
//    {
//        File file = null;
//        Bitmap bitmap = null;
//        file = fileCache.getFileFromFileCache(imageName);
//
//        try
//        {
//            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
//        }
//        catch(FileNotFoundException fileNotFoundException)
//        {
//            //Consume
//        }
//
//        if(bitmap != null)
//        {
//            return bitmap;
//        }
//        else
//        {
//            if(AppUtil.isImageUrlValid(url))
//            {
//                //Download From WS
//                bitmap = ImageManager.downloadBitmap(url, file);
//            }
//        }
//
//        return bitmap;
//    }
//
//}
//
//class BitmapDisplayerRunnable implements Runnable
//{
//    Bitmap bitmap;
//    ImageInfo photoToLoad;
//    public BitmapDisplayerRunnable(Bitmap bitmap, ImageInfo imageInfo)
//    {
//        this.bitmap = bitmap;
//        this.photoToLoad = imageInfo;
//    }
//
//    public void run()
//    {
//        if(imageViewReused(photoToLoad))
//        {
//            return;
//        }
//
//        if(bitmap!=null)
//        {
//            photoToLoad.getImageView().setImageBitmap(bitmap);
//        }
//        else
//        {
//            photoToLoad.getImageView().setImageResource(imagePlaceHolderId);
//        }
//    }
//}