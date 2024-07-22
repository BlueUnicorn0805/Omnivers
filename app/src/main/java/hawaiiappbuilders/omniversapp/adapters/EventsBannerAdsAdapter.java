package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import hawaiiappbuilders.omniversapp.R;

public class EventsBannerAdsAdapter extends RecyclerView.Adapter<EventsBannerAdsAdapter.MyViewHolder> implements View.OnClickListener {

    // Image Loader
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.imageView) {
            if (itemListener != null) {
                int position = (int) v.getTag();
                itemListener.onItemClicked(position);
            }
        } else if(viewId == R.id.ivPlay) {
            MyViewHolder holder = (MyViewHolder) v.getTag();
            int position = holder.getAdapterPosition();

            holder.ivPlay.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);

            holder.webView.setVisibility(View.VISIBLE);
        }
    }

    public interface AdsItemListener {
        void onItemClicked(int position);
    }

    private LayoutInflater inflater;
    private Context ctx;
    private AdsItemListener itemListener;

    private List<String> videoIDs;

    public EventsBannerAdsAdapter(Context ctx, List<String> videoIds, AdsItemListener listener) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.videoIDs = videoIds;
        this.itemListener = listener;

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(ctx.getApplicationContext()));
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false)
                .cacheOnDisk(false)
               /* .showImageOnFail(R.mipmap.ic_launcher1)
                .showImageOnLoading(R.mipmap.ic_launcher1)
                .showImageForEmptyUri(R.mipmap.ic_launcher1)*/
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_event_banner_ads, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        // Display Image
        mImageLoader.displayImage(getVideImageLink(position), holder.imageView, mImageOptions);

        holder.imageView.setVisibility(View.VISIBLE);
        holder.ivPlay.setVisibility(View.VISIBLE);
        holder.webView.setVisibility(View.INVISIBLE);

        // Click Action
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);

        holder.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        holder.webView.setWebChromeClient(new WebChromeClient());
        holder.webView.getSettings().setJavaScriptEnabled(true);
        // holder.webView.getSettings().setAppCacheEnabled(true);
        holder.webView.setInitialScale(1);
        holder.webView.getSettings().setLoadWithOverviewMode(true);
        holder.webView.getSettings().setUseWideViewPort(true);

        holder.webView.setTag(position);
        holder.webView.loadUrl(getVideLink(position));

        holder.ivPlay.setTag(holder);
        holder.ivPlay.setOnClickListener(this);
    }

    public String getVideImageLink(int position) {
        return String.format("http://img.youtube.com/vi/%s/0.jpg", videoIDs.get(position));
    }

    public String getVideLink(int position) {
        return "https://www.youtube.com/embed/" + videoIDs.get(position);
    }

    @Override
    public int getItemCount() {
        return videoIDs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected View ivPlay;
        protected WebView webView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            webView = itemView.findViewById(R.id.webView);

            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
            //webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            webView.setInitialScale(1);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }
    }
}
