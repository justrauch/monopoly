from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, Boolean, text
from sqlalchemy.orm import sessionmaker, declarative_base, relationship
import pymysql

connection = pymysql.connect(
    host="localhost",
    user="root",
    password=""
)

try:
    with connection.cursor() as cursor:
        cursor.execute("CREATE DATABASE IF NOT EXISTS monopoly")
finally:
    connection.close()

SQLALCHEMY_DATABASE_URL = f"mysql+pymysql://root@localhost:3306/monopoly"

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True)
    name = Column(String(50), unique=True, nullable=False)
    password = Column(String(255), nullable=False)

    money = Column(Integer, nullable=False, default=1500)
    turn_number = Column(Integer, nullable=False, default=0)
    position = Column(Integer, nullable=False, default=0)
    figure = Column(Integer, nullable=False, default=0)

    #prison_sentence = Column(Integer, nullable=False, default=0)

    # Matches
    matches_created = relationship(
        "Match",
        back_populates="creater",
        foreign_keys="Match.creater_id"
    )
    matches_second = relationship(
        "Match",
        back_populates="secondplayer",
        foreign_keys="Match.secondplayer_id"
    )
    matches_third = relationship(
        "Match",
        back_populates="thirdplayer",
        foreign_keys="Match.thirdplayer_id"
    )
    matches_fourth = relationship(
        "Match",
        back_populates="fourthplayer",
        foreign_keys="Match.fourthplayer_id"
    )

    # Owned streets
    owner_created = relationship(
        "Street",
        back_populates="owner",
        foreign_keys="Street.owner_id",
        cascade="all, delete-orphan"
    )


class Match(Base):
    __tablename__ = "matches"

    id = Column(Integer, primary_key=True)

    creater_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    secondplayer_id = Column(Integer, ForeignKey("users.id", ondelete="SET NULL"), nullable=True)
    thirdplayer_id = Column(Integer, ForeignKey("users.id", ondelete="SET NULL"), nullable=True)
    fourthplayer_id = Column(Integer, ForeignKey("users.id", ondelete="SET NULL"), nullable=True)

    is_active = Column(Integer, nullable=False, default=0)
    winner_id = Column(Integer, ForeignKey("users.id", ondelete="SET NULL"), nullable=True)

    # Player relations
    creater = relationship("User", foreign_keys=[creater_id], back_populates="matches_created")
    secondplayer = relationship("User", foreign_keys=[secondplayer_id], back_populates="matches_second")
    thirdplayer = relationship("User", foreign_keys=[thirdplayer_id], back_populates="matches_third")
    fourthplayer = relationship("User", foreign_keys=[fourthplayer_id], back_populates="matches_fourth")

    # Winner relation
    winner = relationship("User", foreign_keys=[winner_id])

    # Streets in this match
    match_created = relationship(
        "Street",
        back_populates="match",
        cascade="all, delete-orphan",
        foreign_keys="Street.match_id"
    )


class Street(Base):
    __tablename__ = "streets"

    id = Column(Integer, primary_key=True)

    owner_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    match_id = Column(Integer, ForeignKey("matches.id", ondelete="CASCADE"), nullable=False)

    houses = Column(Integer, nullable=False, default=0)
    hotels = Column(Integer, nullable=False, default=0)
    price = Column(Integer, nullable=False)

    street_index = Column(Integer, nullable=False)

    is_special = Column(Boolean, nullable=False)

    owner = relationship(
        "User",
        foreign_keys=[owner_id],
        back_populates="owner_created"
    )

    match = relationship(
        "Match",
        foreign_keys=[match_id],
        back_populates="match_created"
    )


with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=0"))
    conn.execute(text("DROP TABLE IF EXISTS score_table"))
    conn.execute(text("DROP TABLE IF EXISTS answer_table"))
    conn.execute(text("DROP TABLE IF EXISTS question_table"))
    conn.execute(text("DROP TABLE IF EXISTS quiz_table"))
    conn.execute(text("SET FOREIGN_KEY_CHECKS=1"))
    conn.commit()

Base.metadata.create_all(bind=engine)
