import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

/*
 * A class representing all of the user's save files.
 */
public class SaveList implements DS.Storable {
    public static class SaveListLoadingException extends LoadingException {
        public SaveListLoadingException(String complaint)
        {
            super("SaveList", complaint);
        }
    }

    // How many saves can the user have?
    public static final int N_SAVES = 2;

    public SaveState saves[];

    public SaveList(SaveState save1, SaveState save2)
    {
        saves = new SaveState[] { save1, save2 };
    }

    public SaveList(SaveState onlySave)
    {
        this(onlySave, null);
    }

    public SaveList()
    {
        this((SaveState) null);
    }

    public SaveList(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    public SaveList(Reader reader) throws LoadingException, DS.NonDeserializableException, DS.ParserException, IOException
    {
        load(DS.load(reader));
    }

    // Where to save this list to
    public static final String savePath = "data/";

    public static final String savefilename = "savedata.txt";

    // Serialize and store this list
    public void saveSelf()
    {
        File dataDir = new File(savePath);
        dataDir.mkdir();

        try (FileWriter writer = new FileWriter(savePath + savefilename)) {
            dump().dump(writer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load saves from file
    public static SaveList loadList()
    {
        File dataDir = new File(savePath);
        dataDir.mkdir();

        try (FileReader reader = new FileReader(savePath + savefilename)) {
            return new SaveList(reader);
        }
        catch (FileNotFoundException e) {
            // Just create a new one if one doesn't already exist
            return new SaveList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new SaveList();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof SaveList) {
            SaveList oList = (SaveList) other;
            for (int i = 0; i < N_SAVES; i++) {
                if (!((saves[i] == null && oList.saves[i] == null) || saves[i].equals(oList.saves[i]))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.Root)) {
            throw new SaveListLoadingException("Must be a root node.");
        }

        DS.Root rootNode = (DS.Root) node;
        if (rootNode.complexVal.size() != N_SAVES) {
            throw new SaveListLoadingException("There must be " + N_SAVES + " save files.");
        }

        saves = new SaveState[N_SAVES];
        for (int i = 0; i < N_SAVES; i++) {
            DS.Node saveNode = ((DS.Root) node).complexVal.get(i);
            if (saveNode instanceof DS.IdNode) {
                DS.IdNode nodeVal = (DS.IdNode) saveNode;
                if (nodeVal.isNil()) {
                    saves[i] = null;
                }
                else {
                    throw new SaveListLoadingException("Invalid save state node. Must be map or nil.");
                }
            }
            else if (saveNode instanceof DS.MapNode) {
                saves[i] = new SaveState(saveNode);
            }
        }
    }

    @Override
    public DS.Node dump()
    {
        DS.Root outNode = new DS.Root();
        for (SaveState saveState : saves) {
            if (saveState == null) {
                outNode.add(new DS.IdNode("nil"));
            }
            else {
                outNode.add(saveState.dump());
            }
        }

        return outNode;
    }
}
