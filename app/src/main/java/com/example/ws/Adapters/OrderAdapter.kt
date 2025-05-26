import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ws.Activityes.OrderDetailsActivity
import com.example.ws.Adapters.OrderItemAdapter
import com.example.ws.Model.OrderItem
import com.example.ws.Model.OrderItemDisplay
import com.example.ws.Model.Orders
import com.example.ws.Model.Sneakers
import com.example.ws.R
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val orders: List<Orders>,
    private val orderItemsMap: Map<Int, List<OrderItem>>,
    private val sneakersMap: Map<Int, Sneakers>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_layout, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order, orderItemsMap[order.id] ?: emptyList(), sneakersMap)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        private val tvDeliveryAddress: TextView = itemView.findViewById(R.id.tvDeliveryAddress)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val rvOrderItems: RecyclerView = itemView.findViewById(R.id.rvOrderItems)

        fun bind(order: Orders, items: List<OrderItem>, sneakersMap: Map<Int, Sneakers>) {
            tvOrderDate.text = formatOrderDate(order.orderDate)
            tvTotalAmount.text = "${order.totalAmount} ₽"
            tvDeliveryAddress.text = order.deliveryAddress
            tvStatus.text = order.status

            val itemAdapter = OrderItemAdapter(items.map { item ->
                OrderItemDisplay(item.quantity, sneakersMap[item.sneakerId]?.imageUrl.orEmpty())
            })
            rvOrderItems.adapter = itemAdapter
            rvOrderItems.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

            itemView.setOnClickListener {
                val itemDisplays = items.map { item ->
                    OrderItemDisplay(item.quantity, sneakersMap[item.sneakerId]?.imageUrl.orEmpty())
                }

                val context = itemView.context
                val intent = android.content.Intent(context, OrderDetailsActivity::class.java).apply {
                    putExtra(OrderDetailsActivity.EXTRA_ORDER, order)
                    putExtra(OrderDetailsActivity.EXTRA_ORDER_ITEMS, itemDisplays as java.io.Serializable)
                }
                context.startActivity(intent)
            }
        }
    }

    fun formatOrderDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())

        try {
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return dateString
        }
    }
}