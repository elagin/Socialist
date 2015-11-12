package pasha.elagin.socialist.DataSource.Vk;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pasha.elagin.socialist.MyUtils;
import pasha.elagin.socialist.R;

/**
 * Created by pavel on 12.11.15.
 */
public class VKNewsfeedAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<VKNewsfeedItem> objects;
    final int MAX_TEXT_SIZE = 100;

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

        Date date = p.getDate();
        if(isToday(date))
            ((TextView) view.findViewById(R.id.date_message)).setText("Сегодня");
        else
            ((TextView) view.findViewById(R.id.date_message)).setText(MyUtils.getStringTime(p.getDate(), true));
        ((TextView) view.findViewById(R.id.time_message)).setText(MyUtils.getStringTime(p.getDate(), false));

        String test = p.getText();
        if(test.length() > MAX_TEXT_SIZE)
            ((TextView) view.findViewById(R.id.text_message)).setText(Html.fromHtml(p.getText().substring(0, MAX_TEXT_SIZE)));
        else
            ((TextView) view.findViewById(R.id.text_message)).setText(Html.fromHtml(p.getText()));

        if (p.getSourceName() != null)
            ((TextView) view.findViewById(R.id.text_source)).setText(p.getSourceName());


        ImageView imageView = ((ImageView) view.findViewById(R.id.imageView));
        Picasso.with(ctx).load(p.getSourceAvatar()).into(imageView);
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

    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * <p>Checks if a date is today.</p>
     * @param date the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

    /**
     * <p>Checks if a calendar date is today.</p>
     * @param cal  the calendar, not altered, not null
     * @return true if cal date is today
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    // товар по позиции
    VKNewsfeedItem getProduct(int position) {
        return ((VKNewsfeedItem) getItem(position));
    }
}
