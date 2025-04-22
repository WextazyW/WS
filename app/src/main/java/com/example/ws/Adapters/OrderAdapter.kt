import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ws.Adapters.OrderItemAdapter
import com.example.ws.Model.OrderItem
import com.example.ws.Model.OrderItemDisplay
import com.example.ws.Model.Orders
import com.example.ws.Model.Sneakers
import com.example.ws.R

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
            tvOrderDate.text = order.orderDate
            tvTotalAmount.text = "${order.totalAmount} ₽"
            tvDeliveryAddress.text = order.deliveryAddress
            tvStatus.text = order.status

            val itemAdapter = OrderItemAdapter(items.map { item ->
                OrderItemDisplay(item.quantity, sneakersMap[item.sneakerId]?.imageUrl.orEmpty())
            })
            rvOrderItems.adapter = itemAdapter
            rvOrderItems.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}