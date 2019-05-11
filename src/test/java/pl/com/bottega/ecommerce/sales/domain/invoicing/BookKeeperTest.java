package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    private InvoiceRequest invoiceRequest;
    private BookKeeper bookKeeper;
    private ProductData productData;
    private ClientData clientData;
    private InvoiceFactory invoiceFactory;
    private TaxPolicy taxPolicy;
    private RequestItem requestItem;
    private ProductDataBuilder productDataBuilder;

    @Before public void setup(){
        invoiceFactory = mock(InvoiceFactory.class);
        bookKeeper = new BookKeeper(new InvoiceFactory());
        productDataBuilder = new ProductDataBuilder();
        productData = productDataBuilder.withType(ProductType.FOOD).build();
        clientData = new ClientData(Id.generate(), "Ciosek");
        invoiceRequest = new InvoiceRequest(clientData);
        taxPolicy = mock(TaxPolicy.class);

        when(invoiceFactory.create(any(ClientData.class))).thenReturn(new Invoice(Id.generate(), new ClientData(Id.generate(), "Temp")));
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(1),"TaxTest"));
    }

    @Test public void invoiceRequestWithOnePosition(){
        requestItem = new RequestItem(productData, 1, new Money(1));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(1)));
    }

    @Test public void invoiceRequestWithTwoPosition() {
        invoiceRequest.add(new RequestItem(productData, 1, new Money(2)));
        invoiceRequest.add(new RequestItem(productData, 2, new Money(3)));

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test public void invoiceRequestWithZeroPositionsCallCalculateTaxZeroTimes(){
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(0)));
    }

    @Test public void invoiceRequestWithZeroItemNotInvoikingCalculateTaxMethod(){
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(0)).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test public void invoiceRequestShouldHaveGivenClientData(){
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getClient(),equalTo(clientData));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoiceRequestWithTwoItemInvokingCalculateTaxMethod(){
        invoiceRequest.add(new RequestItem(productData,1,new Money(2,"PL")));
        invoiceRequest.add(new RequestItem(productData,2,new Money(3)));

        bookKeeper.issuance(invoiceRequest, taxPolicy);
    }

    @Test public void invoiceRequestShouldInvokeTaxPolicyOncePerProductType() {

        Money money = new Money(3);

        when(taxPolicy.calculateTax(ProductType.STANDARD, money)).thenReturn(new Tax(money, "23%"));
        when(taxPolicy.calculateTax(ProductType.FOOD, money)).thenReturn(new Tax(money, "46%"));

        requestItem = new RequestItem(productData, 2, money);
        invoiceRequest.add(requestItem);

        productData = productDataBuilder.withType(ProductType.STANDARD).build();
        requestItem = new RequestItem(productData, 5, money);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.STANDARD, money);
        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.FOOD, money);
    }
}
