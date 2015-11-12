package pasha.elagin.socialist.DataSource.Vk;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pasha.elagin.socialist.MyUtils;
import pasha.elagin.socialist.R;

/**
 * Created by pavel on 12.11.15.
 */
public class VKNewsfeedAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<VKNewsfeedItem> objects;

    public VKNewsfeedAdapter(Context ctx, ArrayList<VKNewsfeedItem> objects) {
        this.ctx = ctx;
        this.objects = objects;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.feed_row, parent, false);
        }

        VKNewsfeedItem p = getProduct(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.date_message)).setText(MyUtils.getStringTime(p.getDate(), true));
        ((TextView) view.findViewById(R.id.time_message)).setText(MyUtils.getStringTime(p.getDate(), false));
        ((TextView) view.findViewById(R.id.text_message)).setText(Html.fromHtml(p.getText()));

        if (p.getSourceName() != null)
            ((TextView) view.findViewById(R.id.text_source)).setText(p.getSourceName());
//
//        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
//        // присваиваем чекбоксу обработчик
//        cbBuy.setOnCheckedChangeListener(myCheckChangList);
//        // пишем позицию
//        cbBuy.setTag(position);
//        // заполняем данными из товаров: в корзине или нет
//        cbBuy.setChecked(p.box);
        return view;
    }

    // товар по позиции
    VKNewsfeedItem getProduct(int position) {
        return ((VKNewsfeedItem) getItem(position));
    }
}
