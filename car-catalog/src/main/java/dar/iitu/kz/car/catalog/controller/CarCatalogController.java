package dar.iitu.kz.car.catalog.controller;


import dar.iitu.kz.car.catalog.model.Car;
import dar.iitu.kz.car.catalog.model.CarCatalog;
import dar.iitu.kz.car.catalog.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carcatalog")
public class CarCatalogController {

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping()
    public List<CarCatalog> getAllCars(){
        List<CarCatalog> carCatalogs = new ArrayList<>();
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                "http://localhost:8082/car/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Car>>(){});

        List<Car> cars = response.getBody();

        for (Car car: cars) {

            ResponseEntity<List<Rating>> ratingsRequest = restTemplate.exchange(
                    "http://localhost:8083/history/rating/" + car.getId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Rating>>(){});

            // getAverageRatingByCarId
            List<Rating> history = ratingsRequest.getBody();
            List<Integer> ratings = history.stream().map(Rating::getRating).sequential().collect(Collectors.toList());
            List<String> reviews = history.stream().map(Rating::getReview).sequential().collect(Collectors.toList());

            carCatalogs.add(new CarCatalog(car.getId(),car.getBrand(), car.getColour(), car.getNumber(), ratings, reviews));

        }

        return carCatalogs;
    }


    @GetMapping("/{id}")
    public Car getCar() {

        Car carBook = restTemplate.getForObject("http://localhost:8082/car/info/1", Car.class);

        return carBook;
    }



    @PostMapping()
    public Car createCar() {
        return new Car("1", "Ferrari", "Red", "123123");
    }

}