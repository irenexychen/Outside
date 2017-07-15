package xy.temp.outside;

import android.util.Log;

/**
 * Created by Xiang-Yi on 2016-03-25.
 */
public class Unit {

    public boolean mUsed;
    public int mColour;
    public int IndexX;
    public int IndexY;
    public int mSurrounding;
    public int ImageId;

    Unit mOwnerArray[][];

    public Unit(Unit ownerArray[][], int indexX, int indexY) {
        //link variables from inside constructor
        mOwnerArray = ownerArray;
        IndexX = indexX;
        IndexY = indexY;
    }

    public void AddSurrounding(int xTotal, int yTotal) {
        int newX;
        int newY;

        for (int i = -2; i <= 2; i++) { //5x5
            for (int k = -2; k <= 2; k++) {
                newX = IndexX + i;
                newY = IndexY + k;
                //Log.d("Current Index:", newX + ", " + newY);

                if ((newX >= 0 && newX < xTotal) && (newY >= 0 && newY < yTotal)) {
                    mOwnerArray[newX][newY].mSurrounding++; ///surroundingUsed -> dots that exist within an 5x5 area
                    //Log.d("AddSurrounding", IndexX + ", " + IndexY + "; " + newX + ", " + newY + "; " + mOwnerArray[newX][newY].mSurrounding + "tS");
                }

            }
        }
    }

    public void RemoveSurrounding(int xTotal, int yTotal) {
        int newX;
        int newY;

        for (int i = -2; i <= 2; i++) {
            for (int k = -2; k <= 2; k++) {
                newX = IndexX + i;
                newY = IndexY + k;
                //Log.d("Current Index:", newX + ", " + newY);

                if ((newX >= 0 && newX < xTotal) && (newY >= 0 && newY < yTotal)) {
                    if (mOwnerArray[newX][newY].mSurrounding > 0) {
                        mOwnerArray[newX][newY].mSurrounding--;
                    }
                    //Log.d("RemoveSurrounding", newX + ", " + newY + ", " + mOwnerArray[newX][newY].mSurrounding);
                }
            }
        }
    }

    public void SetImageId (int imageId){
        ImageId = imageId;
    }
}