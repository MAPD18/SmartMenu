package ca.mapd.capstone.smartmenu.restaurant.model;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class CachedRestaurantRepository {

    private CachedRestaurantDao cachedRestaurantDao;

    public CachedRestaurantRepository(Application application) {
        cachedRestaurantDao = AppDatabase.getDatabase(application).cachedRestaurantDao();
    }

    public void insert (CachedRestaurant word) {
        new InsertAsyncTask(cachedRestaurantDao).execute(word);
    }

    private static class InsertAsyncTask extends AsyncTask<CachedRestaurant, Void, Void> {

        private CachedRestaurantDao mAsyncTaskDao;

        InsertAsyncTask(CachedRestaurantDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CachedRestaurant... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void getAllAsync(GetAllAsyncTask.AsyncResponse delegate) {
        new GetAllAsyncTask(cachedRestaurantDao, delegate).execute();
    }

    private static class GetAllAsyncTask extends AsyncTask<Void, Void, List<CachedRestaurant>> {

        public interface AsyncResponse {
            void processFinish(List<CachedRestaurant> output);
        }

        private AsyncResponse delegate;

        private CachedRestaurantDao mAsyncTaskDao;

        GetAllAsyncTask(CachedRestaurantDao dao, AsyncResponse delegate) {
            mAsyncTaskDao = dao;
            this.delegate = delegate;
        }

        @Override
        protected List<CachedRestaurant> doInBackground(Void... voids) {
            return mAsyncTaskDao.getALl();
        }

        @Override
        protected void onPostExecute(List<CachedRestaurant> cachedRestaurants) {
            delegate.processFinish(cachedRestaurants);
        }
    }
}
