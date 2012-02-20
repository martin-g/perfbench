package wicketjpa.wicket;

import java.util.Calendar;

import javax.persistence.Query;

import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import wicketjpa.entity.Booking;
import wicketjpa.entity.Hotel;

@StatelessComponent
public class HotelPage extends TemplatePage {

	public HotelPage(final PageParameters parameters) {
		super(parameters);

		long hotelId = parameters.get("id").toLong();
		Query findHotelQuery = getEntityManager()
				.createQuery("select h from Hotel h where h.id = :hotelId");
		findHotelQuery.setParameter("hotelId", hotelId);
		final Hotel hotel = (Hotel) findHotelQuery.getSingleResult();

		setDefaultModel(new CompoundPropertyModel<Hotel>(hotel));
		add(new Label("name"));
		add(new Label("address"));
		add(new Label("city"));
		add(new Label("state"));
		add(new Label("zip"));
		add(new Label("country"));
		add(new Label("price"));
		StatelessForm form = new StatelessForm("form") {
			@Override
			public void onSubmit() {
				Booking booking = new Booking(hotel, getBookingSession().getUser());
				Calendar calendar = Calendar.getInstance();
				booking.setCheckinDate(calendar.getTime());
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				booking.setCheckoutDate(calendar.getTime());
				setResponsePage(new BookPage(booking));
			}
		};
		add(form);
		form.add(new BookmarkablePageLink<Void>("cancel", MainPage.class));
	}
}
