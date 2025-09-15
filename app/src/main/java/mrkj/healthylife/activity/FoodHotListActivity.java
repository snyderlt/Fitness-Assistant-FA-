package mrkj.healthylife.activity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mrkj.healthylife.R;
import mrkj.healthylife.base.BaseActivity;
import mrkj.healthylife.entity.FoodMessage;
import mrkj.healthylife.entity.FoodType;
import mrkj.healthylife.utils.DBHelper;

public class FoodHotListActivity extends BaseActivity {
    private int sign = -1; // Control the expansion of the list
    private String[] food_type_array; // Food type array
    private List<FoodType> food_list; // Data collection
    private ExpandableListView data_list; // Expandable ListView
    private Bitmap[] bitmaps; // Image resources
    private int[] ids; // Image resource ID array

    /**
     * Set the title bar
     */
    @Override
    protected void setActivityTitle() {
        initTitle();
        setTitle("Food Calorie Comparison Table", this);
        setMyBackGround(R.color.watm_background_gray);
        setTitleTextColor(R.color.theme_blue_two);
        setTitleLeftImage(R.mipmap.mrkj_back_blue);
    }

    /**
     * Set the layout for the interface
     */
    @Override
    protected void getLayoutToView() {
        setContentView(R.layout.activity_food_hot_list);
    }

    /**
     * Initialize data resources
     */
    @Override
    protected void initValues() {
        ids = new int[]{R.mipmap.mrkj_gu, R.mipmap.mrkj_cai,
            R.mipmap.mrkj_guo, R.mipmap.mrkj_rou, R.mipmap.mrkj_dan,
            R.mipmap.mrkj_yv, R.mipmap.mrkj_nai, R.mipmap.mrkj_he,
            R.mipmap.mrkj_jun, R.mipmap.you};
        bitmaps = new Bitmap[ids.length];
        for (int i = 0; i < ids.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), ids[i]);
        }
        food_type_array = new String[]{"Cereal", "Vegetables", "Fruits", "Meat",
                "Eggs", "Seafood", "Dairy", "Drinks", "Fungi", "Oils"};
        food_list = new ArrayList<>();
        // Construct data source
        DBHelper dbHelper = new DBHelper();
        Cursor cursor = dbHelper.selectAllDataOfTable("hot");
        for (int i = 0; i < 10; i++) {
            FoodType foodType = null;
            List<FoodMessage> foods = null;
            int counts = 1;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String hot = cursor.getString(cursor.getColumnIndex("hot"));
                String type_name = cursor.getString(cursor.getColumnIndex("type_name"));
                if (counts == 1) {
                    foodType = new FoodType();
                    foods = new ArrayList<>();
                    foodType.setFood_type(type_name);
                }
                FoodMessage foodMessage = new FoodMessage();
                foodMessage.setFood_name(name);
                foodMessage.setHot(hot);
                foods.add(foodMessage);
                foodType.setFood_list(foods);
                if (counts == 20) {
                    food_list.add(foodType);
                    break;
                }
                counts++;
            }
        }
        cursor.close();
    }

    @Override
    protected void initViews() {
        data_list = (ExpandableListView) findViewById(R.id.food_list);
    }

    /**
     * Bind the adapter
     */
    @Override
    protected void setViewsFunction() {
        MyFoodAdapter adapter = new MyFoodAdapter();
        data_list.setAdapter(adapter);
    }

    /**
     * Set the listener for click events to expand one and collapse others
     */
    @Override
    protected void setViewsListener() {
        data_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                         int groupPosition, long id) {
                if (sign == -1) {
                    // Expand the selected group
                    data_list.expandGroup(groupPosition);
                    // Set the selected group to the top
                    data_list.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                } else if (sign == groupPosition) {
                    data_list.collapseGroup(sign);
                    sign = -1;
                } else {
                    data_list.collapseGroup(sign);
                    // Expand the selected group
                    data_list.expandGroup(groupPosition);
                    // Set the selected group to the top
                    data_list.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                }
                return true;
            }
        });
    }

    /**
     * Adapter class
     */
    class MyFoodAdapter extends BaseExpandableListAdapter {

        // Number of groups
        @Override
        public int getGroupCount() {
            return food_list.size();
        }

        // Number of children in each group
        @Override
        public int getChildrenCount(int groupPosition) {
            return food_list.get(groupPosition).getFood_list().size();
        }

        // Get the group at the specified position
        @Override
        public Object getGroup(int groupPosition) {
            return food_list.get(groupPosition);
        }

        // Get the child at the specified position
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return food_list.get(groupPosition).getFood_list().get(childPosition);
        }

        // Get the group ID at the specified position
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        // Get the child ID at the specified position
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // Whether the ID for a child or group is stable
        @Override
        public boolean hasStableIds() {
            return true;
        }

        // Get the view for a group
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder;
            if (convertView == null) {
                holder = new GroupViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.group_item, null);
                holder.image = (ImageView) convertView.findViewById(R.id.group_image);
                holder.title = (TextView) convertView.findViewById(R.id.group_title);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.image.setImageBitmap(bitmaps[groupPosition]);
            holder.title.setText(food_type_array[groupPosition]);
            return convertView;
        }

        // Get the view for a child
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if (convertView == null) {
                holder = new ChildViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.child_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.food_name);
                holder.hot = (TextView) convertView.findViewById(R.id.food_hot);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }
            FoodMessage food = food_list.get(groupPosition).getFood_list().get(childPosition);
            holder.name.setText(food.getFood_name());
            holder.hot.setText(food.getHot() + " kcal/gram");
            return convertView;
        }

        // Check if the child can be selected
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class GroupViewHolder {
        ImageView image;
        TextView title;
    }

    class ChildViewHolder {
        TextView name, hot;
    }
}
