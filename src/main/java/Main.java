import algorithm.ProposedGeneticAlgorithm;
import algorithm.Simulator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.Common;
import model.*;
import repository.DatasetRepository;
import repository.WarehouseRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<DueTime> dueTimes = createDueTimeHourly();
        int[][] datasetList = {
//                {50,200,3},
                {150,200,3}
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
//        createDataset(wr, datasetList, dueTimes);

//        Run Algorithm
//        runAlgorithm(datasetList, wr.getLocations());

//        Run Simulation
        runSimulation(wr.getLocations(), wr.getWarehouse().getNumberOfRows());
    }

    public static void runAlgorithm(int[][] datasetList, ArrayList<Location> locations) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        int numberOfRun = 1;
        for (int i = 0; i < datasetList.length; i++) {
            int[] list = datasetList[i];
            String filename = list[0]+"-"+list[1]+"-"+list[2]+".json";

            Dataset dataset = mapper.readValue(new File(Common.getResource(filename).getPath()), Dataset.class);

            for (int j = 0; j < numberOfRun; j++) {
                try {
                    ProposedGeneticAlgorithm algo = new ProposedGeneticAlgorithm();
                    algo.setLocations(locations);
                    algo.setDataset(dataset);
                    algo.start();
                    //Converting the Object to JSONString
                    String jsonString = mapper.writeValueAsString(algo.getElite().getBatches());
                    System.out.println(jsonString);
                } catch (CloneNotSupportedException | ParseException e) {
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

    public static void runSimulation(ArrayList<Location> locations, Integer rows) throws IOException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        Batch[] data = mapper.readValue(new File(Common.getResource("simulation.json").getPath()), Batch[].class);
        List<Batch> batches = Arrays.asList(data);
        Simulator simulator = new Simulator();
        simulator.setLocations(locations);
        simulator.setBatches(batches);
        simulator.setNumberOfPicker(2);
        simulator.setRows(rows);
        simulator.start();

        simulator.exportToExcel();
    }

}
