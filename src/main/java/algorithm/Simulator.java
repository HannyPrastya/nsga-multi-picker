package algorithm;

import model.Batch;
import repository.WarehouseRepository;

import java.util.ArrayList;

public class Simulator {
    private WarehouseRepository wr;
    private int numberOfPicker;
    private ArrayList<Batch> batches;

    public Simulator(WarehouseRepository wr, int numberOfPicker, ArrayList<Batch> batches){
        this.wr = wr;
        this.numberOfPicker = numberOfPicker;
        this.batches = batches;
    }

    public void start(){

    }
}
