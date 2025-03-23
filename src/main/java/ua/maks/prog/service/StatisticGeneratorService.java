package ua.maks.prog.service;

import org.springframework.stereotype.Service;
import ua.maks.prog.entity.Counter;
import ua.maks.prog.views.StatisticView;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticGeneratorService {
//      private final CounterService counterService;
//
//        public StatisticGeneratorService(CounterService counterService) {
//            this.counterService = counterService;
//        }
//
//        public File generateStatisticJpeg(StatisticView statisticView) {
//            try {
//                XYChart.Series<String, Number> series = new XYChart.Series<>();
//                series.setName(String.valueOf(LocalDateTime.now().getYear()));
//
//                List<Counter> result = counterService.getAllStatistic();
//                result.sort(Comparator.comparing(Counter::getDateTime));
//
//                if (statisticView == StatisticView.YEARLY) {
//                    Map<Integer, Integer> monthStatistic = calculateAmountByYear(result);
//                    monthStatistic.forEach((year, amount) -> {
//                        series.getData().add(new XYChart.Data<>(year.toString(), amount));
//                    });
//                } else if (statisticView == StatisticView.MONTHLY) {
//                    Map<Month, Integer> monthStatistic = calculateAmountByMonth(result);
//                    monthStatistic.forEach((month, amount) -> {
//                        series.getData().add(new XYChart.Data<>(month.name(), amount));
//                    });
//                } else if (statisticView == StatisticView.WEEKS) {
//                    Map<Integer, Integer> weeksStatistic = calculateAmountByWeek(result);
//                    weeksStatistic.forEach((week, amount) -> {
//                        series.getData().add(new XYChart.Data<>("Week " + week, amount));
//                    });
//                }
//                return saveChartAsJpeg();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        private File saveChartAsJpeg() {
//            WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
//            File file = new File("chart.jpg");
//
//            try {
//                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return file;
//        }
//
//        private Map<Integer, Integer> calculateAmountByYear(List<Counter> data) {
//            Map<Integer, Integer> result = new HashMap<>();
//            for (Counter counter : data) {
//                int year = counter.getDateTime().getYear();
//                result.put(year, result.getOrDefault(year, 0) + counter.getAmount());
//            }
//            return result;
//        }
//
//        private Map<Month, Integer> calculateAmountByMonth(List<Counter> data) {
//            Map<Month, Integer> result = new HashMap<>();
//            for (Counter counter : data) {
//                Month month = counter.getDateTime().getMonth();
//                result.put(month, result.getOrDefault(month, 0) + counter.getAmount());
//            }
//            return result;
//        }
//
//        private Map<Integer, Integer> calculateAmountByWeek(List<Counter> data) {
//            Map<Integer, Integer> result = new HashMap<>();
//            for (Counter counter : data) {
//                int week = counter.getDateTime().getDayOfYear() / 7;
//                result.put(week, result.getOrDefault(week, 0) + counter.getAmount());
//            }
//            return result;
//        }
}
