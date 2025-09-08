public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    // Implement onCreateViewHolder, onBindViewHolder, and getItemCount
    // to inflate product items and bind product data to views.
}

