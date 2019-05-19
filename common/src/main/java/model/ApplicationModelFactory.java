package model;

public class ApplicationModelFactory {

    private static ThreadLocal<ApplicationModel> modelThreadLocal = new ThreadLocal<>();

    private static volatile ApplicationModel mainApplicationModel = null;

    public static ApplicationModel getMainApplicationModel(){
        if(mainApplicationModel == null){
            mainApplicationModel = new ApplicationModel();
        }
        return mainApplicationModel;
    }
}
