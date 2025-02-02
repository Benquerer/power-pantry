package pt.ipt.dam.powerpantry.ui.gallery


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.data.Product

class GalleryRecyclerViewAdapter(
    //product list
    private val productList: List<Product>,
    //lambda for click
    private val onItemClick: (Product) -> Unit
    ) : RecyclerView.Adapter<GalleryRecyclerViewAdapter.ProductViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        //holder and product info
        val product = productList[position]
        holder.productCode.text = "${product.productCode}"
        holder.productName.text = product.productName
        holder.productPrice.text = String.format("%.2f",product.productPrice) + "€"
        holder.productBrand.text = product.productBrand

        //set image from url
        Glide.with(holder.itemView.context).load(product.productImage)
            .placeholder(R.drawable.ic_placeholder_img)
            .error(R.drawable.ic_image_error)
            .into(holder.productImg)

        //onclick event
        holder.itemView.setOnClickListener{
            onItemClick(product)
        }

    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productCode: TextView = itemView.findViewById(R.id.tvProductCode)
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val productBrand: TextView = itemView.findViewById(R.id.tvProductBrand)
        val productImg: ImageView = itemView.findViewById(R.id.ivProductImg)
    }

}