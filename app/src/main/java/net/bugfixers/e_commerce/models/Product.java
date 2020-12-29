package net.bugfixers.e_commerce.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String name;
    private int price;
    private String category;
    private String image;
    private int amount;
    private boolean favorite;

    public Product() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Parcelling part
    public Product(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.name = data[0];
        this.price = Integer.parseInt(data[1]);
        this.category = data[2];
        this.image = data[3];
        this.amount = Integer.parseInt(data[4]);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.name,
                String.valueOf(this.price),
                this.category,
                this.image,
                String.valueOf(this.amount)
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
