package com.davis.tyler.magpiehunt;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.davis.tyler.magpiehunt.Hunts.Award;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FileSystemManager
{
    String intStorageDir;

    public FileSystemManager()
    {
        //TODO: set up files and scanners
    }

    public void initializationCheck(Context c) throws IOException
    {
        //Log.w("Output -- testttt","\n\n\n\n\n"+c.getFilesDir().toString()+"\n\n\n\n\n");
        String intStorageDir = c.getFilesDir().toString();

        this.intStorageDir = intStorageDir;

        File folder = new File(intStorageDir, "images");
        File hunts = new File(intStorageDir, "hunts.txt");
        boolean success = false;
        if(!folder.exists())
            folder.mkdirs();

        if(!hunts.exists())
        {

            success = hunts.createNewFile();

            //Log.w("FILE REPORT!!","CREATED FILE REPORT : "+success);
        }


    }

    public void addHuntList(Context c, LinkedList<Hunt> huntList) throws IOException
    {
        //System.out.println("check: entered method, context: "+c);
        OutputStreamWriter out = new OutputStreamWriter(c.openFileOutput("hunts.txt", Context.MODE_PRIVATE));
        //FileOutputStream out = new FileOutputStream(new File(c.getFilesDir()+"hunts.txt"));

        for (int i = 0; i < huntList.size(); i++) //loads badge info
        {
            //System.out.println("check: entered for loop");
            out.write("********************\r\n");//beginning of hunt marker
            Hunt h = huntList.get(i);
            out.write((h.getIsCompleted()+"\r\n"));
            out.write((h.getID()+"\r\n"));
            out.write((h.getAbbreviation()+"\r\n"));
            out.write((h.getIsAvailable()+"\r\n"));
            out.write((h.getDateStart()+"\r\n"));
            out.write((h.getDateEnd()+"\r\n"));
            out.write((h.getName()+"\r\n"));
            out.write((h.getIsOrdered()+"\r\n"));
            out.write((h.getDescription()+"\r\n"));
            out.write("////////\r\n");
            out.write((h.getCity()+"\r\n"));
            out.write((h.getState()+"\r\n"));
            out.write((h.getZip()+"\r\n"));
            out.write((h.getSponsor()+"\r\n"));
            out.write((h.getRating()+"\r\n"));

            out.write("^^^^^^^^^^^^^^^^^^^^\r\n"); //beginning of award marker
            Award award = huntList.get(i).getAward();
            out.write((award.getID()+"\r\n"));
            out.write((award.getAddress()+"\r\n"));
            out.write((award.getDescription()+"\r\n"));
            out.write("////////\r\n");
            out.write((award.getName()+"\r\n"));
            out.write((award.getLocationDescription()+"\r\n"));
            out.write((award.getLat()+"\r\n"));
            out.write((award.getLong()+"\r\n"));
            out.write((award.getSuperBadgeIcon()+"\r\n"));
            out.write((award.getTerms()+"\r\n"));
            out.write((award.getWorth()+"\r\n"));
            out.write((award.getValue()+"\r\n"));

            LinkedList<Badge> ll = huntList.get(i).getAllBadges();
            for(int j = 0; j < ll.size(); j++)
            {
                Badge toAdd = ll.get(j);
                out.write("--------------------\r\n"); //beginning of badge marker
                out.write((toAdd.getID()+"\r\n"));
                out.write((toAdd.getDescription()+"\r\n"));
                out.write("////////\r\n");
                out.write((toAdd.getIcon()+"\r\n"));
                out.write((toAdd.getLandmarkImage()+"\r\n"));
                out.write((toAdd.getLandmarkName()+"\r\n"));
                out.write((toAdd.getLongitude()+"\r\n"));
                out.write((toAdd.getLatitude()+"\r\n"));
                out.write((toAdd.getName()+"\r\n"));
                out.write((toAdd.getIsCompleted()+"\r\n"));
                out.write((toAdd.getDistance()+"\r\n"));
                out.write((toAdd.getHuntID()+"\r\n"));
                out.write((toAdd.getQRurl()+"\r\n"));

                //System.out.println("writing badge, "+toAdd.getName());
            }
            //System.out.println("writing hunt, "+h.getName());
        }
        out.close();
    }


    public String readHuntsFile(Context c) throws IOException
    {

        String ret = "";

        FileInputStream is = c.openFileInput("hunts.txt");
        if(is != null)
        {

            InputStreamReader in = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(in);
            String newString = "";

            while((newString = br.readLine()) != null)
            {

                ret+=newString+"\n";
            }
            is.close();

        }

        return ret;
    }

    public LinkedList<Hunt> getHuntsFromFile(Context c) throws IOException, ParseException
    {
        FileInputStream is = c.openFileInput("hunts.txt");

        LinkedList<Hunt> huntList = null;
        if(is != null)
        {
            InputStreamReader in = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(in);
            String newString = "";
            boolean cont = true;
            newString = br.readLine();
            System.out.println("check: newStringRead: "+newString);
            if(newString == null)
                cont = false;
            huntList = new LinkedList<>();
            while(cont)
            {
                System.out.println("check: in while loop");
                //HUNT DATA
                newString = br.readLine();
                boolean isCompleted = Boolean.parseBoolean(newString);
                newString = br.readLine();
                int id = Integer.parseInt(newString);
                newString = br.readLine();
                String abbr = newString;
                newString = br.readLine();
                boolean isAvailable = Boolean.parseBoolean(newString);
                newString = br.readLine();
                String dateStart = newString;
                newString = br.readLine();
                String dateEnd = newString;
                newString = br.readLine();
                String name = newString;
                newString = br.readLine();
                boolean isOrdered = Boolean.parseBoolean(newString);
                newString = br.readLine();
                String description = "";
                while(!newString.equals("////////")) {
                    System.out.println("check: in desc1 while");
                    description += newString;
                    newString = br.readLine();
                }
                System.out.println("check: exited while loop");
                newString = br.readLine();
                String city = newString;
                newString = br.readLine();
                String state = newString;
                newString = br.readLine();
                int zip = Integer.parseInt(newString);
                newString = br.readLine();
                String sponsor = newString;
                newString = br.readLine();
                String rating = newString;
                newString = br.readLine();

                //AWARD DATA
                newString = br.readLine();
                int awardID = Integer.parseInt(newString);
                newString = br.readLine();
                String awardAddress = newString;
                newString = br.readLine();
                String awardDescription = "";
                while(!newString.equals("////////")) {
                    System.out.println("check: in desc2 while");
                    awardDescription += newString;
                    newString = br.readLine();

                }
                System.out.println("check: exited while");
                newString = br.readLine();
                String awardName = newString;
                newString = br.readLine();
                String awardLocationDescription = newString;
                newString = br.readLine();
                double awardLat = Double.parseDouble(newString);
                newString = br.readLine();
                double awardLong = Double.parseDouble(newString);
                newString = br.readLine();
                String awardSuperBadge = newString;
                newString = br.readLine();
                String awardTerms = newString;
                newString = br.readLine();
                String awardWorth = newString;
                newString = br.readLine();
                String awardValue = newString;
                newString = br.readLine();
                Award newAward = new Award(awardID, awardAddress, awardDescription, awardName, awardLocationDescription, awardLat, awardLong, awardSuperBadge);

                //GET BADGE INFO
                HashMap<Integer, Badge> badges = new HashMap<>();
                String bdescription = "";
                boolean contBadges = true;
                while(contBadges)
                {
                    newString = br.readLine();
                    //System.out.println("Should be bid: "+newString);
                    int bid = Integer.parseInt(newString);
                    newString = br.readLine();
                    System.out.println("Should be desc: "+newString);
                    while(!newString.equals("////////")) {
                        System.out.println("check: in desc3 while");
                        bdescription += newString;
                        newString = br.readLine();
                    }
                    System.out.println("check: exited desc3 while");
                    newString = br.readLine();
                    String icon = newString;
                    newString = br.readLine();
                    String landmarkImg = newString;
                    System.out.println("limg: "+newString);
                    newString = br.readLine();
                    System.out.println("lName: "+newString);
                    String landmarkName = newString;
                    newString = br.readLine();
                    System.out.println("LONGITUDE: "+newString);
                    double blongitude = Double.parseDouble(newString);
                    newString = br.readLine();
                    double blatitude = Double.parseDouble(newString);
                    newString = br.readLine();
                    String bname = newString;
                    newString = br.readLine();
                    boolean bisCompleted = Boolean.parseBoolean(newString);
                    newString = br.readLine();
                    double bDistance = Double.parseDouble(newString);
                    newString = br.readLine();
                    int bHuntID = Integer.parseInt(newString);
                    newString = br.readLine();
                    String QRurl = newString;
                    newString = br.readLine();
                    Badge newB = new Badge(bid, bdescription, icon, landmarkName, blatitude, blongitude, bname, bisCompleted, bHuntID, landmarkImg);
                    badges.put(bid, newB);
                    if(newString == null)
                        contBadges = false;
                    else if(!newString.equals("--------------------"))
                        contBadges = false;
                }
                Hunt h = new Hunt(badges, newAward, isCompleted, id, abbr, isAvailable, dateStart, dateEnd, name, isOrdered, description, city, state, zip, sponsor);
                huntList.add(h);
                if(newString == null)
                    cont = false;



            }
        }
        is.close();
        System.out.println("HUNTLIST: "+huntList.toString());
        return huntList;
    }
    public void addTestHunt(Context c) throws IOException
    {
        HashMap<Integer, Badge> badges = new HashMap<>();
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.example_badge);
        //Bitmap landmarkpic = BitmapFactory.decodeResource(context.getResources(), R.drawable.ewu);
        String descb1 = "Eastern Washington University was established in 1882 by a $10,000 grant from expressman Benjamin Pierce Cheney, and was originally known as Benjamin P. Cheney Academy to honor its founder. In 1889 the school was renamed State Normal School at Cheney and in 1937 to Eastern Washington College of Education. The campus was almost totally destroyed twice by fire in 1891 and 1912, but was rebuilt each time, and grew quickly in size following World War II. The school became Eastern Washington State College. During this era, Eastern added various graduate and undergraduate degree programs. In 1977, the school's name was changed for the final time to Eastern Washington University by the Washington State Legislature.[15]\n" +
                "\n" +
                "In 1992, the core of the campus was listed on the National Register of Historic Places as the Washington State Normal School at Cheney Historic District.[16][17]";
        Badge badge1 = new Badge(0, descb1,"filepathhere", "landmark1",
                47.4873895,-117.5778622, "badge1", false, 0, "filepathhere");
        Badge badge2 = new Badge(1, "testbadge2","filepathhere", "landmark2",
                47.4873897,-117.5757624, "badge2", false, 0, "filepathhere");
        Badge badge3 = new Badge(2, "testbadge3","filepathhere", "landmark3",
                47.4862000,-117.5757111, "badge3", false, 0, "filepathhere");

        //Bitmap bitmapSuperBadge = BitmapFactory.decodeResource(context.getResources(), R.drawable.example_super_badge);
        Award award = new Award(0, "2702 AL OGDON WAY CHENEY, WA", "testaward1",
                "award1", "this is my apartment area", 47.4872000,-117.5757111,
                "filepathhere");
        badges.put(badge1.getID(), badge1);
        badges.put(badge2.getID(), badge2);
        badges.put(badge3.getID(), badge3);
        int noOfDays = 14; //i.e two weeks
        Date dateOfStart = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfStart);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date dateOfEnd = calendar.getTime();
        Hunt hunt1 = new Hunt(badges, award, false, 0,"HNT", true,
                dateOfStart.toString(), dateOfEnd.toString(), "hunt1", false, "summary: this is hunt1",
                "CHENEY", "WA", 99004, "CJ's");
        hunt1.setIsFocused(true);

    }


    /*************************IMAGES********************************/

    public void saveImageToInternalStorage(Context c, Bitmap bitmapImage, String filename) throws FileNotFoundException, IOException
    {


        //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        System.out.println("here1");
        File mypath=new File(c.getFilesDir()+"/images",filename);
        System.out.println("here2: "+mypath);
        FileOutputStream fos = null;

        fos = new FileOutputStream(mypath);
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        System.out.println("here3: "+fos);

        fos.close();
    }

    public Bitmap loadImageFromStorage(Context c, String filename) throws FileNotFoundException
    {
        Bitmap b = null;

        File f=new File(c.getFilesDir()+"/images/", filename);
        System.out.println("here4: "+f);
        b = BitmapFactory.decodeStream(new FileInputStream(f));
        System.out.println("here5: "+b);
        //ImageView img=(ImageView)findViewById(R.id.imgPicker);
        //img.setImageBitmap(b);


        return b;
    }

    public void printImagesDirectory(Context c)
    {
        File f = new File(c.getFilesDir()+"/images");
        File[] files = f.listFiles();

        for(File newFile : files)
        {
            System.out.println("FILE: "+ newFile.getName());
        }
    }
}
