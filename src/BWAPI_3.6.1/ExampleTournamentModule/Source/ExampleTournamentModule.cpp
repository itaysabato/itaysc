#include <windows.h>
#include <Shlwapi.h>
#include "ExampleTournamentModule.h"
using namespace BWAPI;

bool leader = false;

DWORD dwHash = 0;
void ExampleTournamentAI::onStart()
{
  // Set the command optimization level (reduces high APM, size of bloated replays, etc)
  Broodwar->setCommandOptimizationLevel(MINIMUM_COMMAND_OPTIMIZATION);

  for each (Unit *u in Broodwar->getStaticNeutralUnits())
    dwHash += u->getInitialPosition().x() * u->getInitialType() | u->getInitialHitPoints() ^ u->getInitialResources() + u->getPosition().y();

  char szModuleFileName[MAX_PATH];
  GetModuleFileName((HMODULE)hMod, szModuleFileName, MAX_PATH);
  HANDLE hFile = CreateFile(szModuleFileName, GENERIC_READ, FILE_SHARE_READ, NULL, 0, 0, NULL);
  if ( hFile )
  {
    dwHash |= GetFileSize(hFile, NULL);
    CloseHandle(hFile);
  }
}

int maxAPM;
void ExampleTournamentAI::onEnd(bool isWinner)
{
  // save maxAPM or something
}

void ExampleTournamentAI::onFrame()
{
  int thisAPM = Broodwar->getAPM();
  if ( thisAPM > maxAPM )
    maxAPM = thisAPM;

}

void ExampleTournamentAI::onSendText(std::string text)
{
}

void ExampleTournamentAI::onReceiveText(BWAPI::Player* player, std::string text)
{
  if ( player != Broodwar->self() )
  {
    if ( strncmp(text.c_str(), "Welcome to " TOURNAMENT_NAME "! HASH: ", strlen("Welcome to " TOURNAMENT_NAME "! HASH: ")) == 0 )
    {
      const char *pszhash = strstr(text.c_str(), "HASH: ");
      if ( pszhash )
      {
        DWORD dwHashTest = 0;
        sscanf_s(&pszhash[6], "%X", &dwHashTest);
        if ( dwHashTest == dwHash )
          Broodwar->sendText("Hash Accepted.");
        else
          Broodwar->sendText("Hash Denied. Modified tournament module detected.");
      }
    }
  }
}

void ExampleTournamentAI::onPlayerLeft(BWAPI::Player* player)
{
}

void ExampleTournamentAI::onNukeDetect(BWAPI::Position target)
{
}

void ExampleTournamentAI::onUnitDiscover(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitEvade(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitShow(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitHide(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitCreate(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitDestroy(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitMorph(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onUnitRenegade(BWAPI::Unit* unit)
{
}

void ExampleTournamentAI::onSaveGame(std::string gameName)
{
}

// Use some existing Battle.net filters
const char *pszBadWords[] =
{ 
  "asshole",
  "bitch",
  "clit",
  "cock",
  "cunt",
  "dick",
  "dildo",
  "faggot",
  "fuck",
  "gook",
  "masturbat",
  "nigga",
  "nigger",
  "penis",
  "pussy",
  "shit",
  "slut",
  "whore",
  NULL
};

// as well as the Battle.net swear word filter algorithm
const char szBadWordCharacters[] = { '!', '@', '#', '$', '%', '&' };
void BadWordFilter(char *pszString)
{
  // Iterate each badword
  for ( int f = 0; pszBadWords[f]; ++f )
  {
    // Find badword
    char *pszMatch = StrStrI(pszString, pszBadWords[f]);
    if ( !pszMatch )
      continue; // continue if badword not found

    // iterate characters in badword
    char cLast = 0;
    for ( int i = 0; pszBadWords[f][i]; ++i )
    {
      // make the character compatible with our replacements
      int val = pszBadWords[f][i] & 7;
      if ( val >= sizeof(szBadWordCharacters) )
        val = 0;

      // increment the replacement if it's the same as our last one, reset to 0 if it's out of bounds
      if ( cLast == szBadWordCharacters[val] && ++val == sizeof(szBadWordCharacters) )
        val = 0;

      // apply our change to the original string and save the last character used
      pszMatch[i] = szBadWordCharacters[val];
      cLast       = szBadWordCharacters[val];
    }
  }
}

bool ExampleTournamentModule::onAction(int actionType, void *parameter)
{
  switch ( actionType )
  {
  case Tournament::SendText:
  case Tournament::Printf:
    // Call our bad word filter and allow the AI module to send text
    BadWordFilter((char*)parameter);
    return true;
  case Tournament::EnableFlag:
    switch ( *(int*)parameter )
    {
    case Flag::CompleteMapInformation:
    case Flag::UserInput:
      // Disallow these two flags
      return false;
    }
    // Allow other flags if we add more that don't affect gameplay specifically
    return true;
  case Tournament::LeaveGame:
  case Tournament::PauseGame:
  case Tournament::RestartGame:
  case Tournament::ResumeGame:
  case Tournament::SetFrameSkip:
  case Tournament::SetGUI:
  case Tournament::SetLocalSpeed:
  case Tournament::SetMap:
    return false; // Disallow these actions
  case Tournament::ChangeRace:
  case Tournament::SetLatCom:
  case Tournament::SetTextSize:
    return true; // Allow these actions
  case Tournament::SetCommandOptimizationLevel:
    return *(int*)parameter > MINIMUM_COMMAND_OPTIMIZATION; // Set a minimum command optimization level 
                                                            // to reduce APM with no action loss
  default:
    break;
  }
  return true;
}

void ExampleTournamentModule::onFirstAdvertisement()
{
  leader = true;
  Broodwar->sendText("Welcome to " TOURNAMENT_NAME "! HASH: 0x%08X", dwHash);
  Broodwar->sendText("Brought to you by " SPONSORS ".");
}
