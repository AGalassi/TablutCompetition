package it.unibo.ai.didattica.competition.tablut.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import it.unibo.ai.didattica.competition.tablut.exceptions.*;

public class GameAshtonTablut implements Game {

	/**
	 * Number of repeated states that can occur before a draw
	 */
	private int movesDraw;
	/**
	 * Counter for the moves without capturing that have occurred
	 */
	private int movesWithutCapturing;
	private String gameLogName;
	private File gameLog;
	private FileHandler fh;
	private Logger loggGame;
	private List<String> citadels;
	//private List<String> strangeCitadels;
	private List<State> drawConditions;
	
	public GameAshtonTablut(int moves) {
		super();
		this.setMovesDraw(moves);
		this.setMovesWithutCapturing(0);
		this.gameLogName = (new Date().getTime())+"_gameLog.txt";
		this.setGameLog(new File(this.gameLogName));
		fh = null;
		try
		{
			fh = new FileHandler(gameLogName, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		this.loggGame = Logger.getLogger("GameLog");
		loggGame.addHandler(this.fh);
		this.fh.setFormatter(new SimpleFormatter());
		loggGame.setLevel(Level.FINE);
		loggGame.fine("Inizio partita");
		this.setDrawConditions(new ArrayList<State>());
		this.citadels = new ArrayList<String>();
		//this.strangeCitadels = new ArrayList<String>();
		this.citadels.add("a4");
		this.citadels.add("a5");
		this.citadels.add("a6");
		this.citadels.add("b5");
		this.citadels.add("d1");
		this.citadels.add("e1");
		this.citadels.add("f1");
		this.citadels.add("e2");
		this.citadels.add("i4");
		this.citadels.add("i5");
		this.citadels.add("i6");
		this.citadels.add("h5");
		this.citadels.add("d9");
		this.citadels.add("e9");
		this.citadels.add("f8");
		this.citadels.add("e8");
		//this.strangeCitadels.add("e1");
		//this.strangeCitadels.add("a5");
		//this.strangeCitadels.add("i5");
		//this.strangeCitadels.add("e9");		
	}
	
	@Override
	public State checkMove(State state, Action a) throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException, ThroneException, OccupitedException, ClimbingCitadelException, CitadelException 
	{
		int dimPareggioStati=1;
		this.loggGame.fine(a.toString());
		//controllo la mossa
		if(a.getTo().length()!=2 || a.getFrom().length()!=2)
		{
			this.loggGame.warning("Formato mossa errato");
			throw new ActionException(a);
		}
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();
		
		//controllo se sono fuori dal tabellone
		if(columnFrom>state.getBoard().length-1 || rowFrom>state.getBoard().length-1 || rowTo>state.getBoard().length-1 || columnTo>state.getBoard().length-1 || columnFrom<0 || rowFrom<0 || rowTo<0 || columnTo<0)
		{
			this.loggGame.warning("Mossa fuori tabellone");
			throw new BoardException(a);			
		}
		
		//controllo che non vada sul trono
		if(state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString()))
		{
			this.loggGame.warning("Mossa sul trono");
			throw new ThroneException(a);
		}
		
		//controllo la casella di arrivo
		if(!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString()))
		{
			this.loggGame.warning("Mossa sopra una casella occupata");
			throw new OccupitedException(a);
		}
		if(this.citadels.contains(state.getBox(rowTo, columnTo)) && !this.citadels.contains(state.getBox(rowFrom, columnFrom)))
		{
			this.loggGame.warning("Mossa che arriva sopra una citadel");
			throw new CitadelException(a);
		}
		if(this.citadels.contains(state.getBox(rowTo, columnTo)) && this.citadels.contains(state.getBox(rowFrom, columnFrom)))
		{
			if(rowFrom==rowTo)
			{
				if(columnFrom-columnTo>5 || columnFrom-columnTo<-5)
				{
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(a);
				}
			}
			else
			{
				if(rowFrom-rowTo>5 || rowFrom-rowTo<-5)
				{
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(a);
				}
			}
			
		}
		
		//controllo se cerco di stare fermo
		if(rowFrom==rowTo && columnFrom==columnTo)
		{
			this.loggGame.warning("Nessuna mossa");
			throw new StopException(a);
		}
		
		//controllo se sto muovendo una pedina giusta
		if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		{
			if(!state.getPawn(rowFrom, columnFrom).equalsPawn("W") && !state.getPawn(rowFrom, columnFrom).equalsPawn("K"))
			{
				this.loggGame.warning("Giocatore "+a.getTurn()+" cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}
		if(state.getTurn().equalsTurn(State.Turn.BLACK.toString()))
		{
			if(!state.getPawn(rowFrom, columnFrom).equalsPawn("B"))
			{
				this.loggGame.warning("Giocatore "+a.getTurn()+" cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}
		
		//controllo di non muovere in diagonale
		if(rowFrom != rowTo && columnFrom != columnTo)
		{
			this.loggGame.warning("Mossa in diagonale");
			throw new DiagonalException(a);
		}
		
		
		//controllo di non scavalcare pedine
		if(rowFrom==rowTo)
		{
			if(columnFrom>columnTo)
			{
				for(int i=columnTo; i<columnFrom; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						if(state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString()))
						{
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						}
						else 
						{
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(this.citadels.contains(state.getBox(rowFrom, i)) && !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom())))
					{
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
			else
			{
				for(int i=columnFrom+1; i<=columnTo; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						if(state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString()))
						{
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						}
						else 
						{
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(this.citadels.contains(state.getBox(rowFrom, i)) && !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom())))
					{
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
		}
		else
		{
			if(rowFrom>rowTo)
			{
				for(int i=rowTo; i<rowFrom; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						if(state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
						{
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						}
						else 
						{
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(this.citadels.contains(state.getBox(i, columnFrom)) && !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom())))
					{
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
			else
			{
				for(int i=rowFrom+1; i<=rowTo; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()))
					{
						if(state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
						{
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						}
						else 
						{
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(this.citadels.contains(state.getBox(i, columnFrom)) && !this.citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom())))
					{
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
		}
		
		//se sono arrivato qui, muovo la pedina
		state = this.movePawn(state, a);
		
		//a questo punto controllo lo stato per eventuali catture
		if(state.getTurn().equalsTurn("W"))
		{
			state = this.checkCaptureBlack(state, a);
		}
		if(state.getTurn().equalsTurn("B"))
		{
			state = this.checkCaptureWhite(state, a);
		}
		
		this.loggGame.fine("Stato: "+state.toString());
		
		//controllo pareggio ulteriore
		if(state.getTurn().equals(State.Turn.WHITE.toString()))
		{
			this.drawConditions.add(state);
			int trovati=0;
			for(int i=0; i<this.drawConditions.size(); i++)		
			{
				if(this.drawConditions.get(i).equals(state))
				{
					trovati++;
				}
				if(trovati==dimPareggioStati)
				{
					state.setTurn(State.Turn.DRAW);
					this.loggGame.fine("Partita terminata in pareggio per numero di stati ripetuti");
				}
			}
			if(this.drawConditions.size()>20)
			{
				this.drawConditions.remove(0);
			}
		}
				
		return state;
	}
	
	
	private State checkCaptureWhite(State state, Action a)
	{
		//controllo se mangio a destra
		if(a.getColumnTo()<state.getBoard().length-2 && state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("B") && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("K")||this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()+2))))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo()+1);
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
		}
		//controllo se mangio a sinistra
		if(a.getColumnTo()>1 && state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("B") && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("K")||this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()-2))))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo()-1);
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
		}
		//controllo se mangio sopra
		if(a.getRowTo()>1 && state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("B") && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("K")||this.citadels.contains(state.getBox(a.getRowTo()-2, a.getColumnTo()))))
		{
			state.removePawn(a.getRowTo()-1, a.getColumnTo());
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
		}
		//controllo se mangio sotto
		if(a.getRowTo()<state.getBoard().length-2 && state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("B") && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("K")||this.citadels.contains(state.getBox(a.getRowTo()+2, a.getColumnTo()))))
		{
			state.removePawn(a.getRowTo()+1, a.getColumnTo());
			this.movesWithutCapturing=-1;
			this.loggGame.fine("Pedina nera rimossa in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
		}
		//controllo se ho vinto
		if(a.getRowTo()==0 || a.getRowTo()==state.getBoard().length-1 || a.getColumnTo()==0 || a.getColumnTo()==state.getBoard().length-1)
		{
			if(state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K"))
			{
				state.setTurn(State.Turn.WHITEWIN);
				this.loggGame.fine("Bianco vince con re in "+a.getTo());
			}
		}
		
		//controllo il pareggio
		if(this.movesWithutCapturing>=this.movesDraw && (state.getTurn().equalsTurn("B")||state.getTurn().equalsTurn("W")))
		{
			state.setTurn(State.Turn.DRAW);
			this.loggGame.fine("Stabilito un pareggio per troppe mosse senza mangiare");
		}
		this.movesWithutCapturing++;
		return state;
	}
		
	private State checkCaptureBlack(State state, Action a)
	{
		//controllo se mangio a destra
		if(a.getColumnTo()<state.getBoard().length-2 && (state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K")) && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T")||this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()+2))))
		{
			//nero-re-trono N.B. No indexOutOfBoundException perchè se il re si trovasse sul bordo il giocatore bianco avrebbe già vinto
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()+1))) && (state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()+1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}
			//nero-re-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K") && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()+2))))
			{
				//mangio il re?
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
					}	
				}						
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()+1))) && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
				}
			}			
			//nero-bianco-trono/nero/citadel
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo(), a.getColumnTo()+1);
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()+1));
			}
			
		}
		//controllo se mangio a sinistra
		if(a.getColumnTo()>1 && (state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K")) && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T")||this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()-2))))
		{
			//trono-re-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()-1))) && (state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()-1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			}
			//nero-re-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K") && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()-2))))
			{
				//mangio il re?
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
					}
				}
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()-1))) && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && (state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()-1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
			}
			//trono/nero/citadel-bianco-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo(), a.getColumnTo()-1);
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
			}
		}
		//controllo se mangio sopra
		if(a.getRowTo()>1 && (state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K")) && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T")||this.citadels.contains(state.getBox(a.getRowTo()-2, a.getColumnTo()))))
		{
			//nero-re-trono 
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()-1))) && (state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()+1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}			
			//nero-re-nero
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K") && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-2, a.getColumnTo()))))
			{
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()-1))) && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T") && (state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()-1, a.getColumnTo()+1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
				}
				//mangio il re?
				if(!state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo(), a.getColumnTo()-1));
					}
				}
			}			
			//nero-bianco-trono/nero/citadel
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo()-1, a.getColumnTo());
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo()-1, a.getColumnTo()));
			}
		}
		//controllo se mangio sotto
		if(a.getRowTo()<state.getBoard().length-2 && (state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K")) && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T")||this.citadels.contains(state.getBox(a.getRowTo()+2, a.getColumnTo()))))
		{
			//nero-re-trono
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()-1))) && (state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()+1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
			}			
			//nero-re-nero
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K") && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+2, a.getColumnTo()))))
			{
				//ho circondato su 3 lati il re?
				if((state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()-1))) && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && (state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B")||this.citadels.contains(state.getBox(a.getRowTo()+1, a.getColumnTo()+1))))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
				//mangio il re?
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && !state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
						this.loggGame.fine("Nero vince con re catturato in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
					}
				}
			}		
			//nero-bianco-trono/nero
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo()+1, a.getColumnTo());
				this.movesWithutCapturing=-1;
				this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(a.getRowTo()+1, a.getColumnTo()));
			}			
		}
		//controllo il re completamente circondato
		if(state.getPawn(4, 4).equalsPawn(State.Pawn.KING.toString()) && state.getBoard().length==9)
		{
			if(state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
				this.loggGame.fine("Nero vince con re catturato sul trono");
			}
		}
		if(state.getPawn(3, 3).equalsPawn(State.Pawn.KING.toString()) && state.getBoard().length==7)
		{
			if(state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(2, 3).equalsPawn("B") && state.getPawn(3, 2).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
				this.loggGame.fine("Nero vince con re catturato sul trono");
			}
		}
		//controllo regola 11
		if(state.getBoard().length==9)
		{
			if(a.getColumnTo()==4 && a.getRowTo()==2)
			{
				if(state.getPawn(3, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B"))
				{
					state.removePawn(3, 4);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(3, 4));
				}
			}
			if(a.getColumnTo()==4 && a.getRowTo()==6)
			{
				if(state.getPawn(5, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B") && state.getPawn(3, 4).equalsPawn("B"))
				{
					state.removePawn(5, 4);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(5, 4));
				}
			}
			if(a.getColumnTo()==2 && a.getRowTo()==4)
			{
				if(state.getPawn(4, 3).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(3, 4).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B"))
				{
					state.removePawn(4, 3);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(4, 3));
				}
			}
			if(a.getColumnTo()==6 && a.getRowTo()==4)
			{
				if(state.getPawn(4, 5).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(3, 4).equalsPawn("B"))
				{
					state.removePawn(4, 5);
					this.movesWithutCapturing=-1;
					this.loggGame.fine("Pedina bianca rimossa in: "+state.getBox(4, 5));
				}
			}
		}
		
		
		//controllo il pareggio
		if(this.movesWithutCapturing>=this.movesDraw && (state.getTurn().equalsTurn("B")||state.getTurn().equalsTurn("W")))
		{
			state.setTurn(State.Turn.DRAW);
			this.loggGame.fine("Stabilito un pareggio per troppe mosse senza mangiare");
		}
		this.movesWithutCapturing++;
		return state;
	}
	
	private State movePawn(State state, Action a) {
		State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = state.getBoard();
		//State newState = new State();
		this.loggGame.fine("Movimento pedina");
		//libero il trono o una casella qualunque
		if(a.getColumnFrom()==4 && a.getRowFrom()==4)
		{
			newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.THRONE;
		}
		else
		{
			newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.EMPTY;
		}
		
		//metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()]=pawn;
		//aggiorno il tabellone
		state.setBoard(newBoard);
		//cambio il turno
		if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		{
			state.setTurn(State.Turn.BLACK);
		}
		else
		{
			state.setTurn(State.Turn.WHITE);
		}
		
		
		return state;
	}
	
	public File getGameLog() {
		return gameLog;
	}

	public void setGameLog(File gameLog) {
		this.gameLog = gameLog;
	}

	public int getMovesWithutCapturing() {
		return movesWithutCapturing;
	}

	public void setMovesWithutCapturing(int movesWithutCapturing) {
		this.movesWithutCapturing = movesWithutCapturing;
	}

	public int getMovesDraw() {
		return movesDraw;
	}

	public void setMovesDraw(int movesDraw) {
		this.movesDraw = movesDraw;
	}

	public List<State> getDrawConditions() {
		return drawConditions;
	}

	public void setDrawConditions(List<State> drawConditions) {
		this.drawConditions = drawConditions;
	}

	
}
