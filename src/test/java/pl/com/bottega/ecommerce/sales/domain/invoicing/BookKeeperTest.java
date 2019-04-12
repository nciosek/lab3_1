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
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    InvoiceFactory invoiceFactory;
    ClientData clientData;
    InvoiceRequest invoiceRequest;
    BookKeeper bookKeeper;

    @Before public void setup(){
        invoiceFactory = new InvoiceFactory();
        clientData = new ClientData(Id.generate(), "Ciosek");
        invoiceRequest = new InvoiceRequest(clientData);
        bookKeeper = new BookKeeper(invoiceFactory);
    }

    @Test public void invoiceRequestWithZeroPosition(){
        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(3))).thenReturn(new Tax(new Money(0.23), "23%"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(0)));
    }

    @Test public void invoiceRequestWithOnePosition(){
        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(3))).thenReturn(new Tax(new Money(0.23), "23%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(3));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(1)));
    }

    @Test public void invoiceRequestWithTwoPosition() {
        Money money1 = new Money(3);
        Money money2 = new Money(5);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, money1)).thenReturn(new Tax(new Money(0.23), "23%"));
        when(taxPolicy.calculateTax(ProductType.FOOD, money2)).thenReturn(new Tax(new Money(0.46), "46%"));

        ProductData productData1 = mock(ProductData.class);
        when(productData1.getType()).thenReturn(ProductType.STANDARD);

        ProductData productData2 = mock(ProductData.class);
        when(productData2.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem1 = new RequestItem(productData1, 5, new Money(3));
        invoiceRequest.add(requestItem1);

        RequestItem requestItem2 = new RequestItem(productData2, 2, new Money(5));
        invoiceRequest.add(requestItem2);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(2)));
    }

    @Test public void invoiceRequestWithZeroPositionsCallCalculateTaxZeroTimes(){
        Money money = new Money(4);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.FOOD, money)).thenReturn(new Tax(new Money(0.46), "46%"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(0)).calculateTax(ProductType.FOOD, money);
    }

    @Test public void invoiceRequestWithTwoPositionsCallCalculateTaxTwoTimes(){
        Money money1 = new Money(4);
        Money money2 = new Money(7);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, money1)).thenReturn(new Tax(new Money(0.23), "23%"));
        when(taxPolicy.calculateTax(ProductType.FOOD, money2)).thenReturn(new Tax(new Money(0.46), "46%"));

        ProductData productData1 = mock(ProductData.class);
        when(productData1.getType()).thenReturn(ProductType.STANDARD);

        ProductData productData2 = mock(ProductData.class);
        when(productData2.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem1 = new RequestItem(productData1, 5, new Money(4));
        invoiceRequest.add(requestItem1);

        RequestItem requestItem2 = new RequestItem(productData2, 2, new Money(7));
        invoiceRequest.add(requestItem2);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.STANDARD, money1);
        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.FOOD, money2);
    }

    @Test public void invoiceRequestWithThreePositionsCallCalculateTaxThreeTimes(){
        Money money1 = new Money(4);
        Money money2 = new Money(6);
        Money money3 = new Money(7);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, money1)).thenReturn(new Tax(new Money(0.23), "23%"));
        when(taxPolicy.calculateTax(ProductType.FOOD, money2)).thenReturn(new Tax(new Money(0.46), "46%"));
        when(taxPolicy.calculateTax(ProductType.DRUG, money3)).thenReturn(new Tax(new Money(0.38), "38%"));

        ProductData productData1 = mock(ProductData.class);
        when(productData1.getType()).thenReturn(ProductType.STANDARD);

        ProductData productData2 = mock(ProductData.class);
        when(productData2.getType()).thenReturn(ProductType.FOOD);

        ProductData productData3 = mock(ProductData.class);
        when(productData3.getType()).thenReturn(ProductType.DRUG);

        RequestItem requestItem1 = new RequestItem(productData1, 2, new Money(4));
        invoiceRequest.add(requestItem1);

        RequestItem requestItem2 = new RequestItem(productData2, 8, new Money(6));
        invoiceRequest.add(requestItem2);

        RequestItem requestItem3 = new RequestItem(productData3, 4, new Money(7));
        invoiceRequest.add(requestItem3);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.STANDARD, money1);
        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.FOOD, money2);
        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.DRUG, money3);
    }
}
