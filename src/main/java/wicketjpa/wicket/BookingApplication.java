package wicketjpa.wicket;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.convert.converter.BigDecimalConverter;

public class BookingApplication extends WebApplication {

	private EntityManagerFactory emf;

	@Override
	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}

	@Override
	public void init() {
		super.init();

		emf = Persistence.createEntityManagerFactory("bookingDatabase");

		getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy() {

			public boolean isActionAuthorized(Component c, Action a) {
				return true;
			}

			public boolean isInstantiationAuthorized(Class clazz) {
				if (TemplatePage.class.isAssignableFrom(clazz)) {
					if (BookingSession.get().getUser() == null) {
						throw new RestartResponseException(HomePage.class);
					}
				}
				return true;
			}
		});
		getMarkupSettings().setCompressWhitespace(true);
		mountPage("/home", HomePage.class);
		mountPage("/logout", LogoutPage.class);
		mountPage("/register", RegisterPage.class);
		mountPage("/settings", PasswordPage.class);

		getRequestCycleListeners().add(new JpaRequestCycleListener());
	}

	@Override
	public BookingSession newSession(Request request, Response response) {
		return new BookingSession(request);
	}

	public final EntityManagerFactory getEntityManagerFactory()
	{
		return emf;
	}

	public static BookingApplication get()
	{
		return (BookingApplication) Application.get();
	}

	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator converterLocator = new ConverterLocator();
		BigDecimalConverter converter = new BigDecimalConverter() {
			@Override
			public NumberFormat getNumberFormat(Locale locale) {
				return DecimalFormat.getCurrencyInstance(Locale.US);
			}
		};
		converterLocator.set(BigDecimal.class, converter);
		return converterLocator;
	}
}
