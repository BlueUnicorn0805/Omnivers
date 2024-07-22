package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import hawaiiappbuilders.omniversapp.MenuListActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.MenuHeader;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.SideDish;

import java.util.ArrayList;

public class MenuItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        MenuHeaderViewHolder.HeaderViewHolderCallback {

    private LayoutInflater inflater;
    private static final int REST_IMAGE = 4;
    private static final int MENU_ITEM = 1;
    private static final int MENU_ITEM_NO = 2;
    private static final int MENU_HEADER = 3;
    public ArrayList<MenuHeader> headerList;
    public ArrayList<MenuItem> itemList;
    public ArrayList<SideDish> sidesList;
    private Context ctx;
    private MenuListActivity menuListActivity;

    private SparseArray<ViewType> viewTypes;
    private SparseIntArray headerExpandTracker;

    ImageLoader _imageLoader;
    DisplayImageOptions _imageOptions;

    public MenuItemAdapter(Context ctx, ArrayList<MenuItem> itemList, ArrayList<MenuHeader> headerList, ArrayList<SideDish> sides) {

        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.headerList = headerList;
        this.sidesList = sides;
        this.ctx = ctx;
        this.menuListActivity = (MenuListActivity) ctx;

        viewTypes = new SparseArray<>(itemList.size() + headerList.size());
        headerExpandTracker = new SparseIntArray(headerList.size());

        // Initialize the ImageLoader
        _imageLoader = ImageLoader.getInstance();
        _imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
        _imageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                /*.showImageOnLoading(R.drawable.outdoorseating)
                .showImageOnFail(R.drawable.outdoorseating)
                .showImageForEmptyUri(R.drawable.outdoorseating)*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case REST_IMAGE:
                view = view = inflater.inflate(R.layout.item_menu_restimage, parent, false);
                return new MenuRestImageHolder(view);
            case MENU_ITEM:
                view = view = inflater.inflate(R.layout.item_menu_options, parent, false);
                return new MenuViewHolder(view);
            case MENU_ITEM_NO:
                view = view = inflater.inflate(R.layout.item_menu, parent, false);
                return new MenuViewNOHolder(view);
            case MENU_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_menu_header, parent, false);
                return new MenuHeaderViewHolder(view, this);
            default:
                view = view = inflater.inflate(R.layout.item_menu, parent, false);
                return new MenuViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        ViewType viewType = viewTypes.get(position);
        if (itemViewType == REST_IMAGE) {
            bindMenuImageViewHolder(holder, viewType);
        } else if (itemViewType == MENU_ITEM) {
            bindMenuViewHolder(holder, viewType);
        } else if (itemViewType == MENU_ITEM_NO) {
            bindMenuViewHolderNO(holder, viewType);
        } else if (itemViewType == MENU_HEADER) {
            bindMenuHeaderViewHolder(holder, position, viewType);
        }
    }

    private void bindMenuImageViewHolder(RecyclerView.ViewHolder holder, ViewType viewType) {
        int dataIndex = viewType.getDataIndex();
        MenuRestImageHolder headerViewHolder = (MenuRestImageHolder) holder;

        String videoUrlFormat = "http://img.youtube.com/vi/" + headerList.get(dataIndex).get_headerTitle() + "/0.jpg";
        _imageLoader.displayImage(videoUrlFormat, headerViewHolder.menuRestImage, _imageOptions);
    }

    private void bindMenuHeaderViewHolder(RecyclerView.ViewHolder holder, int position, ViewType viewType) {
        int dataIndex = viewType.getDataIndex();
        MenuHeaderViewHolder headerViewHolder = (MenuHeaderViewHolder) holder;
        headerViewHolder.MenuHeader.setText(headerList.get(dataIndex).get_headerTitle());
        if (isExpanded(position)) {
            headerViewHolder.MenuHeader
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, headerViewHolder.arrowUp, null);
        } else {
            headerViewHolder.MenuHeader
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, headerViewHolder.arrowDown, null);
        }
    }

    private void bindMenuViewHolder(final RecyclerView.ViewHolder holder, ViewType viewType) {
        int dataIndex = viewType.getDataIndex();

        ((MenuViewHolder) holder).menuItemImg.setImageResource(itemList.get(dataIndex).get_imgResId());
        ((MenuViewHolder) holder).menuItemName.setText(itemList.get(dataIndex).get_name());
        ((MenuViewHolder) holder).menuItemPrice.setText(itemList.get(dataIndex).get_price());
        ((MenuViewHolder) holder).menuItemDescription.setText(itemList.get(dataIndex).get_description());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx, R.array.cookingtemp, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((MenuViewHolder) holder).spnCookTemp.setAdapter(adapter);

        ((MenuViewHolder) holder).rcvSides.setLayoutManager(new GridLayoutManager(ctx, 2));
        sidesListAdapter sidesadapter = new sidesListAdapter(ctx, sidesList);
        ((MenuViewHolder) holder).rcvSides.setAdapter(sidesadapter);

        ((MenuViewHolder) holder).btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show the options if not shown, hide if shown.
                if (((MenuViewHolder) holder).clOptions.getVisibility() == View.GONE) {
                    ((MenuViewHolder) holder).clOptions.setVisibility(View.VISIBLE);
                    ((MenuViewHolder) holder).btnAddToCart.setText("Close Options");
                } else {
                    ((MenuViewHolder) holder).clOptions.setVisibility(View.GONE);
                    ((MenuViewHolder) holder).btnAddToCart.setText("Customize");
                }

            }
        });

        ((MenuViewHolder) holder).btnAddToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ctx, "Added to Transaction", Toast.LENGTH_LONG).show();

                ((MenuViewHolder) holder).clOptions.setVisibility(View.GONE);
                ((MenuViewHolder) holder).btnAddToCart.setText("Customize");
            }
        });
    }

    private void bindMenuViewHolderNO(final RecyclerView.ViewHolder holder, ViewType viewType) {
        final int dataIndex = viewType.getDataIndex();

        ((MenuViewNOHolder) holder).menuItemImg.setImageResource(itemList.get(dataIndex).get_imgResId());
        ((MenuViewNOHolder) holder).menuItemName.setText(itemList.get(dataIndex).get_name());
        ((MenuViewNOHolder) holder).menuItemPrice.setText(itemList.get(dataIndex).get_price());
        ((MenuViewNOHolder) holder).menuItemDescription.setText(itemList.get(dataIndex).get_description());
        ((MenuViewNOHolder) holder).txtQuantity.setText(String.valueOf(itemList.get(dataIndex).get_quantity()));

        ((MenuViewNOHolder) holder).btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(((MenuViewNOHolder) holder).txtQuantity.getText().toString());
                val++;

                itemList.get(dataIndex).set_quantity(val);
                ((MenuViewNOHolder) holder).txtQuantity.setText(Integer.toString(val));

                menuListActivity.updatePrice();
            }
        });


        ((MenuViewNOHolder) holder).btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(((MenuViewNOHolder) holder).txtQuantity.getText().toString());
                if (val > 0) {
                    val--;

                    itemList.get(dataIndex).set_quantity(val);
                    ((MenuViewNOHolder) holder).txtQuantity.setText(Integer.toString(val));

                    menuListActivity.updatePrice();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (headerList != null && itemList != null) {
            viewTypes.clear();
            int collapsedCount = 0;
            for (int i = 0; i < headerList.size(); i++) {
                if (headerList.get(i).get_headerType().equals("img")) {
                    viewTypes.put(count, new ViewType(i, REST_IMAGE));
                    count += 1;
                } else {
                    viewTypes.put(count, new ViewType(i, MENU_HEADER));
                    count += 1;
                    String userType = headerList.get(i).get_headerTitle();
                    int childCount = getChildCount(userType);
                    if (headerExpandTracker.get(i) != 0) {
                        // Expanded State
                        for (int j = 0; j < childCount; j++) {
                            if (itemList.get(count - (i + 1) + collapsedCount).is_hasOptions()) {
                                viewTypes.put(count, new ViewType(count - (i + 1) + collapsedCount, MENU_ITEM));
                            } else {
                                viewTypes.put(count, new ViewType(count - (i + 1) + collapsedCount, MENU_ITEM_NO));
                            }

                            count += 1;
                        }
                    } else {
                        // Collapsed
                        collapsedCount += childCount;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (viewTypes.get(position).getType() == MENU_HEADER) {
            return MENU_HEADER;
        } else if (viewTypes.get(position).getType() == MENU_ITEM_NO) {
            return MENU_ITEM_NO;
        } else if (viewTypes.get(position).getType() == REST_IMAGE) {
            return REST_IMAGE;
        } else {
            return MENU_ITEM;
        }
    }

    private int getChildCount(String type) {
        int retCount = 0;
        for (MenuItem m : itemList) {
            if (m.get_category().equals(type)) {
                retCount++;
            }
        }
        return retCount;
    }

    @Override
    public void onHeaderClick(int position) {

        ViewType viewType = viewTypes.get(position);
        int dataIndex = viewType.getDataIndex();
        String userType = headerList.get(dataIndex).get_headerTitle();
        int childCount = getChildCount(userType);
        if (headerExpandTracker.get(dataIndex) == 0) {
            // Collapsed. Now expand it
            headerExpandTracker.put(dataIndex, 1);
            notifyItemRangeInserted(position + 1, childCount);
        } else {
            // Expanded. Now collapse it
            headerExpandTracker.put(dataIndex, 0);
            notifyItemRangeRemoved(position + 1, childCount);
        }

        for (int i = 0; i < headerList.size(); i++) {
            if (i != dataIndex && headerExpandTracker.get(i) == 1) {
                userType = headerList.get(i).get_headerTitle();
                childCount = getChildCount(userType);
                headerExpandTracker.put(i, 0);
                notifyItemRangeRemoved(position + 1, childCount);
            }
        }
    }

    @Override
    public boolean isExpanded(int position) {
        int dataIndex = viewTypes.get(position).getDataIndex();
        return headerExpandTracker.get(dataIndex) == 1;
    }
}
