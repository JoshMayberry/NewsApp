package com.example.android.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

/**
 * Holds data pertaining to a list item for a {@link NewsActivity.NewsAdapter}
 * This is a Generic class that is meant to be extended by a child class.
 * <p>
 * Some methods (such as {@link #}) have a default action.
 * They can be overridden by children to modify behavior.
 * <p>
 * Use: https://stackoverflow.com/questions/18204190/java-abstract-classes-returning-this-pointer-for-derived-classes/39897781#39897781
 */
public class NewsContainer extends BaseObservable {
	private String LOG_TAG = NewsContainer.class.getSimpleName();
	private Context context;

	//See: https://developer.android.com/reference/java/text/DateFormat.html
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat dateImportFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	private static final DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

	private Date date = null;
	private String title = null;
	private String author = null;
	private String section = null;
	private String subText = null;
	private String urlPage = null;
	private String urlImage = null;

	@NonNull
	@Override
	public String toString() {
		return "NewsContainer{" +
				"author='" + author + '\'' +
				", title='" + title + '\'' +
				", subText='" + subText + '\'' +
				", date=" + date +
				", section='" + section + '\'' +
				", urlPage='" + urlPage + '\'' +
				", urlImage='" + urlImage + '\'' +
				'}';
	}

	NewsContainer(Context context) {
		this.context = context;
	}

	//Getters
	//See: https://codelabs.developers.google.com/codelabs/android-databinding/index.html?index=..%2F..index#6
	//See: Android Data Binding Library - Update UI using Observable objects: https://www.youtube.com/watch?v=gP_zj-CIBvM
	@Bindable
	public String getTitle() {
		return title;
	}

	@Bindable
	public String getSubText() {
		return subText;
	}

	@Bindable
	public String getAuthor() {
		if (author == null) {
			return context.getString(R.string.error_author);
		}
		return author;
	}

	@Bindable
	public String getSection() {
		return section;
	}

	@Bindable
	public String getDate() {
		return dateFormat.format(date);
	}

	@Bindable
	public String getTime() {
		return timeFormat.format(date);
	}

	@Bindable
	public String getImage() {
		return urlImage;
	}

	Uri getPage() {
		return QueryUtilities.createUri(urlPage);
	}

	//Setters
	NewsContainer setAuthor(String text) {
		this.author = text;
		return this;
	}

	NewsContainer setSection(String text) {
		this.section = text;
		return this;
	}

	NewsContainer setTitle(String text) {
		this.title = text;
		return this;
	}

	NewsContainer setSubText(String text) {
		this.subText = text;
		return this;
	}

	//See: https://stackoverflow.com/questions/10286204/the-right-json-date-format/15952652#15952652
	//Use: https://stackoverflow.com/questions/8573250/android-how-can-i-convert-string-to-date/8573343#8573343
	NewsContainer setDate(String dateRaw) {
		try {
			this.date = dateImportFormat.parse(dateRaw);
		} catch (ParseException error) {
			Log.e(LOG_TAG, "Parse Date Error", error);
			this.date = null;
		}
		return this;
	}

	NewsContainer setUrlPage(String text) {
		this.urlPage = text;
		return this;
	}

	NewsContainer setUrlImage(String text) {
		this.urlImage = text;
		return this;
	}

	/**
	 * Unlike Picasso, Glide stores the images after resizing them. Because these images will be used only once, this is preferable.
	 * See: https://medium.com/@multidots/glide-vs-picasso-930eed42b81d#67f3
	 * See: https://medium.com/@multidots/glide-vs-picasso-930eed42b81d#439e
	 * See: https://www.andplus.com/blog/fresco-vs-picasso-vs-glide
	 * <p>
	 * Images are loaded using a {@link BindingAdapter}. Data binding links this method with {@link #setUrlImage}.
	 * The view is passed in, along with {@link #urlImage}. Glide is then used to load the image.
	 * If provided, an error image, fallback image, and placeholder image can be used.
	 * See: https://mutualmobile.com/resources/using-data-binding-api-in-recyclerview
	 * See: https://blog.stylingandroid.com/data-binding-part-3/#crayon-5c9d2ab08749d595055862
	 * See: https://android.jlelse.eu/loading-images-with-data-binding-and-picasso-555dad683fdc
	 * See: https://github.com/bumptech/glide/wiki/Loading-and-Caching-on-Background-Threads#user-content-into
	 * See: https://stackoverflow.com/questions/34607028/how-to-set-image-resource-to-imageview-using-databinding/37570135#37570135
	 * See: https://stackoverflow.com/questions/35809290/set-drawable-resource-id-in-androidsrc-for-imageview-using-data-binding-in-andr/52983092#52983092
	 * Use: https://developer.android.com/topic/libraries/data-binding/binding-adapters#custom-logic
	 * <p>
	 * Example uses:
	 * <pre>
	 *     <ImageView
	 *         app:imageUrl="@{container.imageUrl}" />
	 *
	 *     <ImageView
	 *         app:imageUrl="@{container.imageUrl}"
	 *         app:imageError="@{@drawable/imageError}"
	 *         app:imageFallback="@{@drawable/imageFallback}"
	 *         app:imagePlaceholder="@{@drawable/imagePlaceholder}" />
	 * </pre>
	 */
	@BindingAdapter(value = {"imageUrl", "imageError", "imageFallback", "imagePlaceholder", "imageCropType", "imageCrossFade"}, requireAll = false)
	public static void loadImage(ImageView view, String imageUrl,
								 Drawable imageError, Drawable imageFallback, Drawable imagePlaceholder,
								 String imageCropType, Integer imageCrossFade) {
		if ((imageUrl == null) && (imageFallback == null)) {
			//There is no image given and no fallback to show instead.
			return;
		}
		//Start Request
		DrawableRequestBuilder<String> builder = Glide.with(view.getContext())
				.load(imageUrl);

		//Apply Settings
		if (imageCrossFade == null) {
			builder.crossFade();
		} else if (imageCrossFade != -1) {
			//See: https://futurestud.io/tutorials/glide-placeholders-fade-animations#transitions
			builder.crossFade(imageCrossFade); //How many milliseconds to fade in at
		}

		if (imageError != null) {
			//See: https://futurestud.io/tutorials/glide-placeholders-fade-animations#errorplaceholderwitherror
			builder.error(imageError); //Displayed if the url cannot be loaded
		}

		if (imageFallback != null) {
			//See: https://futurestud.io/tutorials/glide-placeholders-fade-animations#fallbackplaceholder
			builder.fallback(imageFallback); //What is shown if imageUrl is null
		}

		if (imagePlaceholder != null) {
			//See: https://futurestud.io/tutorials/glide-placeholders-fade-animations#placeholderswithplaceholder
			builder.placeholder(imagePlaceholder); //Displayed while loading url
		}

		//See: https://github.com/bumptech/glide/wiki/Transformations#user-content-default-transformations
		if (imageCropType != null) {
			switch (imageCropType) {
				case "centerCrop":
					builder.centerCrop();
					break;
				case "fitCenter":
					builder.fitCenter();
					break;
			}
		}
		//Finish
		builder.into(view);
	}
}
