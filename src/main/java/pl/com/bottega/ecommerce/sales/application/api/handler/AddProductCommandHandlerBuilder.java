package pl.com.bottega.ecommerce.sales.application.api.handler;

import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerBuilder {
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;

    public AddProductCommandHandlerBuilder setReservationRepository(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
        return this;
    }

    public AddProductCommandHandlerBuilder setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
        return this;
    }

    public AddProductCommandHandlerBuilder setSuggestionService(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
        return this;
    }

    public AddProductCommandHandlerBuilder setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        return this;
    }

    public AddProductCommandHandlerBuilder setSystemContext(SystemContext systemContext) {
        this.systemContext = systemContext;
        return this;
    }

    public AddProductCommandHandler build() {
        return new AddProductCommandHandler(reservationRepository, productRepository, suggestionService, clientRepository, systemContext);
    }

}
