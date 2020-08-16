import algorithm.NonSortedGeneticAlgorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.Common;
import model.Dataset;
import model.DueTime;
import model.Item;
import model.Warehouse;
import repository.DatasetRepository;
import repository.WarehouseRepository;

import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<DueTime> dueTimes = createDueTimeHourly();
        int[][] datasetList = {
//                {50,200,2},
                {50,200,3}
        };

        for (DueTime d: dueTimes) {
            System.out.println(d.getId()+" ==== "+d.getTime());
        }

        WarehouseRepository wr = new WarehouseRepository();
        wr.getWarehouse().setNumberOfRows(4);
        wr.getWarehouse().setNumberOfHorizontalAisle(1);
        wr.getWarehouse().setNumberOfVerticalAisle(2);

        wr.createLocations();
        System.out.println("Number of Locations : "+(wr.getLocations().size() - 1));
        System.out.println("Number of Aisles : "+(wr.getWarehouse().getNumberOfVerticalAisle()+wr.getWarehouse().getNumberOfHorizontalAisle()));

//        Create Dataset
        createDataset(wr, datasetList, dueTimes);

//        Run Algorithm
        runAlgorithm(datasetList);
    }

    public static void runAlgorithm(int[][] datasetList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        int numberOfRun = 1;
        for (int i = 0; i < datasetList.length; i++) {
            int[] list = datasetList[i];
            String filename = list[0]+"-"+list[1]+"-"+list[2]+".json";

            Dataset dataset = mapper.readValue(new File(Common.getResource(filename).getPath()), Dataset.class);

            for (int j = 0; j < numberOfRun; j++) {
                try {
                    NonSortedGeneticAlgorithm algo = new NonSortedGeneticAlgorithm();
                    algo.setDataset(dataset);
                    algo.start();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createDataset(WarehouseRepository wr, int[][] datasetList, ArrayList<DueTime> dueTimes) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        wr.createRandomItems();
        System.out.println("Number of Items : "+wr.getItems().size());

        ArrayList<Item> items = wr.getItems();

        for (int[] list : datasetList) {
//            get dataset
            DatasetRepository dr = new DatasetRepository();
//            set dataset
            Dataset dataset = dr.getDataset();
            dataset.setNumberOfOrders(list[0]);
            dataset.setCapacity(list[1]);
            dataset.setNumberOfItemPerOrder(list[2]);
            dataset.setItems(items);
            dataset.setDueTimes(dueTimes);
            dr.createRandomOrders();

            String json = mapper.writeValueAsString(dataset);

            try {
                try (Writer writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                        "./src/main/resources/" + list[0] + "-" + list[1] + "-" + list[2] + ".json"
                                ),
                                StandardCharsets.UTF_8
                        )
                )
                ) {
                    writer.write(json);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<DueTime> createDueTimeHourly() throws ParseException {
        ArrayList<DueTime> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(Common.convertStringToDate("2020-01-01 08:00:00"));

        for (int i = 1; i <= 4; i++) {
            DueTime t = new DueTime();
            t.setId(i);
            cal.add(Calendar.HOUR, 1);
            t.setTime(Common.convertDateToString(cal.getTime()));
            list.add(t);
        }

        return list;
    }
}
