package managment.system.app.adapter.in.grpc;

import app.grpc.book.ReactorBookServiceGrpc;
import app.grpc.book_types.BookTypes;
import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import managment.system.app.application.BookService;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class GrpcBookService extends ReactorBookServiceGrpc.BookServiceImplBase {

    private final BookService service;

    @Override
    public Flux<BookTypes.Book> getAll(Empty request) {
        return service.getAll()
                .map(GrpcBookMapper.mapper::toProtoEntity);
    }

    @Override
    public Mono<BookTypes.getByIdResponse> getById(Mono<BookTypes.getByIdRequest> request) {
        return request
                .flatMap(req ->
                        service.getById(convertToUUID(req.getId())))
                .map(val -> BookTypes.getByIdResponse.newBuilder()
                        .setBook(GrpcBookMapper.mapper.toProtoEntity(val))
                        .build());
    }

    @Override
    public Mono<BookTypes.saveResponse> save(Mono<BookTypes.saveRequest> request) {
        return request
                .flatMap(req ->
                        service.save(GrpcBookMapper.mapper.toDtoFromProto(req.getDto())))
                .map(val -> BookTypes.saveResponse.newBuilder()
                        .setBook(GrpcBookMapper.mapper.toProtoEntity(val))
                        .build());

    }

    @Override
    public Mono<Empty> delete(Mono<BookTypes.deleteRequest> request) {
        return request
                .flatMap(req ->
                        service.delete(convertToUUID(req.getId())));
    }

    @Override
    public Mono<BookTypes.updateResponse> update(Mono<BookTypes.updateRequest> request) {
        return request
                .flatMap(req ->
                        service.update(convertToUUID(req.getId()), GrpcBookMapper.mapper.toDtoFromProto(req.getDto())))
                .map(val -> BookTypes.updateResponse.newBuilder()
                        .setBook(GrpcBookMapper.mapper.toProtoEntity(val))
                        .build());

    }

    @Override
    public Mono<BookTypes.sellResponse> sell(Mono<BookTypes.sellRequest> request) {
        return request
                .flatMap(req ->
                        service.sell(convertToUUID(req.getId()), req.getQuantity()))
                .map(val -> BookTypes.sellResponse.newBuilder()
                        .setQuantity(val.getQuantity())
                        .build());
    }

    @Override
    public Mono<BookTypes.receiveResponse> receive(Mono<BookTypes.receiveRequest> request) {
        return request
                .flatMap(req ->
                        service.receive(convertToUUID(req.getId()), req.getQuantity()))
                .map(val -> BookTypes.receiveResponse.newBuilder()
                        .setQuantity(val.getQuantity())
                        .build());
    }

    private UUID convertToUUID(BookTypes.UUID id) {
        return UUID.fromString(id.getValue());
    }
}