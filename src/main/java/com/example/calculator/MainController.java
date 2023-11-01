package com.example.calculator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    private final Map<String, Map<String, Double>> CurrencyCoefficients;

    public MainController() {
        String rub = "RUB";
        String dollar = "USD";
        String euro = "EUR";

        CurrencyCoefficients = new HashMap<>();
        CurrencyCoefficients.put(rub, new HashMap<>());
        Map<String, Double> rubCoefficients = CurrencyCoefficients.get(rub);
        rubCoefficients.put(rub, 1d);
        rubCoefficients.put(dollar, 0.010867);
        rubCoefficients.put(euro, 0.010211);

        CurrencyCoefficients.put(dollar, new HashMap<>());
        Map<String, Double> dollarCoefficients = CurrencyCoefficients.get(dollar);
        dollarCoefficients.put(rub, 92.02);
        dollarCoefficients.put(dollar, 1d);
        dollarCoefficients.put(euro, 0.939634);

        CurrencyCoefficients.put(euro, new HashMap<>());
        Map<String, Double> euroCoefficients = CurrencyCoefficients.get(euro);
        euroCoefficients.put(rub, 97.93);
        euroCoefficients.put(dollar, 1.06);
        euroCoefficients.put(euro, 1d);
    }

    @GetMapping

    public String index() {
        return "redirect:/calc";
    }

    @GetMapping("/calc")
    public String openCalculator() {
        return "/calc";
    }

    @PostMapping("/calc")
    public String calculate(@RequestParam double a, @RequestParam double b, @RequestParam String op, Model model) {
        String operationSign = op.toUpperCase();

        double result = switch (operationSign) {
            case "SUM" -> a + b;
            case "SUBTRACT" -> a - b;
            case "MULTIPLY" -> a * b;
            case "DIVIDE" -> a / b;
            default -> {
                model.addAttribute("error", "Неизвестная операция");
                yield 0;
            }
        };

        if (model.getAttribute("error") != null) return "calc";

        model.addAttribute("result", result);
        return "calc";
    }

    @GetMapping("/converter")
    public String openConverter() {
        return "converter";
    }


    @PostMapping("/converter")
    public String convert(
            @RequestParam double from,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            Model model
    ) {
        String fromCurrencyUpperCase = fromCurrency.toUpperCase();
        String toCurrencyUpperCase = toCurrency.toUpperCase();

        try {
            double coefficient = CurrencyCoefficients.get(fromCurrencyUpperCase).get(toCurrencyUpperCase);
            double resulting = from * coefficient;
            model.addAttribute("result", resulting);
        } catch (Exception ignored) {
            model.addAttribute("error", "Не удалось преобразовать строку");
        }
        return "converter";
    }
}
