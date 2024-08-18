package tfar.nations3;

public class zinfo {

    //This is going to be a lot of info at once but it is pretty much the same concepts just in more detail. (will probably be 2 parts)
    //
    //Town Owners -
    //Town is created by buying a chunk of a set fee
    //Chunks are paid for at a set fee every 7 days
    //Further chunks can be claimed under the same rules
    //
    //Money System
    //Utilising the currency from Create: Numismatics, a town bank is implemented that the town owner and any trusted players pay into to pay for chunks.
    //Town members must pay taxes every {amount} day that can be set by the town owner or trusted.
    //2.5% nation tax paid out of chunk payment of towns in a nation
    //
    //Nation Owners -
    //Nation can be created by being a town owner of 5+ players. 2.5% tax on all chunks that are now under a nation. Command like “/onbnation create {name}”
    //Must also pay a set fee
    //Can invite other towns into the nation and also accept requests of other towns asking to join
    //
    //Rank System
    //0-5 players in a town = Town members have the rank of “Villager”, Owner has the rank of “Village Chief”.
    //5-10 players in a town = Town members have the rank of “Resident”, Owner has the rank of “Mayor”.
    //10+ players in a town = Town members have the rank of “Citizen”, Owner has the rank “Governor”.
    //Nation leader has the rank of “President”.
    //
    //Rebellion System
    //Nation member can create a rebellion with a set fee.
    //Can start with 25% players, can only be joined by other nation members.
    //If over 50% of rebellion and non rebellion players online, rebellion can be started by rebellion owner and begin in the chunk created in.
    //
    //War System
    //War chest addition
    //Nation leader/trusted can put money in
    //If enough money in war chest, war can be started for a set fee with another nation.
    //
    //Warring System
    //A neighbouring chunk of a nation-owned chunk will become contested on both sides.
    //Whichever nation has more kills after a timer (e.g 30 minutes), in the nearby radius of the contested chunk moves into the nation chunk and contests that.
    //The chunk must also be paid for from the war chest, if there is failure to pay, the kill timer for the failed chunk will reset and be done over again.
    //If chunk is paid for, a nation chunk is contested, system repeats until a nation is fully claimed by one or opposing side.
    //As another option, a nation can surrender early or nations can agree to a forfeit to either keeps the chunks fought for so far or end without any chunks taken from  eachother.
    //
    //Alliance System
    //Nations can make an alliance and start wars same way, or make an alliance and join in later, another contested chunk simply happens in whichever other nation joins. Friendly fire is turned off by default.
    //
    //*Rebellions - They work similarly however, they can start anywhere in the nation and do not have a war chest. They can be ended the same way. They can happen at the same time as a war, no logic is needed to prevent that.
    //
    //Final point
    //Town leaders can put chunks up together for auction or sell to a specific player for town members to have autonomy within their town.
}
