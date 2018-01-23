package utils;

/**
 * Created by Isaac on 7/23/17.
 */
public class IdService {

    private int currentId;
    private static IdService instance;

    private IdService() {
        currentId = 0;
    }

    public static IdService getInstance() {
        if (instance == null) {
            instance = new IdService();
        }
        return instance;
    }

    public int generateId() {
        return currentId++;
    }

}
