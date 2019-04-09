package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    InvoiceFactory invoiceFactory = new InvoiceFactory();
    ClientData clientData = new ClientData(Id.generate(), "nazwisko");
    InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

    @Mock TaxPolicy taxPolicy;

    //test stanu
    @Test public void emptyInvoice(){
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems(), empty());
    }

    //test zachowania
    @Test public void emptyInvoiceNoCallTaxPolicy(){
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(0)).calculateTax(any(ProductType.class), any(Money.class));
    }


}
