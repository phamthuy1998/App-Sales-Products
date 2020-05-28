package com.thuypham.ptithcm.mytiki.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.thuypham.ptithcm.mytiki.R
import java.math.RoundingMode
import java.text.DecimalFormat


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(7)))
            .error(R.drawable.noimg)
            .into(view)
    } else {
        view.setImageResource(R.drawable.noimg)
    }
}

@BindingAdapter("imageOrderResource")
fun bindImageRes(view: ImageView, type: Int?) {
    when (type) {
        1 -> view.setImageResource(R.drawable.ic_circle_arrow)
        // shipping
        2 -> view.setImageResource(R.drawable.ic_shipping)
        // order success
        3 -> view.setImageResource(R.drawable.ic_success)
        // order cancel
        4 -> view.setImageResource(R.drawable.ic_error)
    }
}

@BindingAdapter("txtStatusOrder")
fun setTextStatusOrder(view: TextView, type: Int?) {
    when (type) {
        1 -> view.text = view.context.getString(R.string.status_1)
        // shipping
        2 -> view.text = view.context.getString(R.string.status_2)
        // order success
        3 -> view.text = view.context.getString(R.string.status_3)
        // order cancel
        4 -> view.text = view.context.getString(R.string.status_4)
    }
}

@BindingAdapter("txtStatusChange")
fun changeStatus(view: TextView, type: Int?) {
    when (type) {
        1 -> {
            view.text = view.context.getString(R.string.change1)
        }
        // shipping
        2 -> {
            view.text = view.context.getString(R.string.change2)
        }
        3 -> view.text = view.context.getString(R.string.status_3)
        // order cancel
        4 -> view.text = view.context.getString(R.string.status_4)
    }
}

@BindingAdapter("txtAccountRole")
fun setTextAcc(view: TextView, type: Int?) {
    when (type) {
        1 -> view.text = view.context.resources.getStringArray(R.array.role)[0]
        // shipping
        2 -> view.text = view.context.resources.getStringArray(R.array.role)[1]
        // order success
        3 -> view.text = view.context.resources.getStringArray(R.array.role)[2]
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textDiscount")
fun bindTextDiscount(view: TextView, discount: Float?) {
    if (discount != 0.toFloat()) {
        view.text = " - " + discount.toString() + "%"
    }
}

@BindingAdapter(value = ["price", "discount"], requireAll = false)
fun bindTextPrice(view: TextView, price: Long?, discount: Float) {
    val df = DecimalFormat("#,###,###")
    df.roundingMode = RoundingMode.CEILING
    val priceSale = price?.minus(((discount * 0.01) * price))
    val priceSelling = df.format(priceSale) + " đ"
    view.text = priceSelling
}

@BindingAdapter("txtPrice")
fun bindPrice(view: TextView, price: Long?) {
    val df = DecimalFormat("#,###,###")
    df.roundingMode = RoundingMode.CEILING
    val priceSelling = df.format(price) + " đ"
    view.text = priceSelling
    view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

@BindingAdapter("totalPrice")
fun bindTotalPrice(view: TextView, price: Long?) {
    val df = DecimalFormat("#,###,###")
    df.roundingMode = RoundingMode.CEILING
    val priceSelling = df.format(price) + " đ"
    view.text = view.context.getString(R.string.totalPriceRe, priceSelling)
}

@BindingAdapter("txtPriceNoStrike")
fun binPriceNoCeiling(view: TextView, price: Long?) {
    if (price != null) {
        val df = DecimalFormat("#,###,###")
        val priceSelling = df.format(price) + " đ"
        view.text = priceSelling
    } else {
        view.text = "0"
    }
}

@BindingAdapter("productStock")
fun bindProductCount(view: TextView, stock: Int) {
    val strStock: String
    if (stock > 0) {
        strStock = "$stock sản phẩm sẵn có"
        view.setTextColor(Color.parseColor("#0C0C0C"))
    } else {
        strStock = "Hết hàng"
        view.setTextColor(Color.parseColor("#F44336"))
    }
    view.text = strStock
}

@BindingAdapter("enableBtnAddCart")
fun bindBtnAddCart(view: TextView, stock: Int) {
    view.isEnabled = stock > 0
}

@BindingAdapter("txtCartCount")
fun bindTextCartCount(view: TextView, counter: Int) {
    if (counter > 0) {
        view.visible()
        view.text = counter.toString()
    } else view.gone()
}