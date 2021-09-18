import { PortfolioIntro } from "../../@types";
import { PORTFOLIO } from "../../constants/localStorageKey";
import { setPortfolioLocalUpdateTime } from "../../storage/storage";
import useLocalStorage from "./@common/useLocalStorage";

const usePortfolioIntro = (name?: string, description?: string) => {
  const { itemState: portfolioIntro, setItem } = useLocalStorage<PortfolioIntro>(PORTFOLIO.INTRO, {
    name: name ?? "",
    isProfileShown: true,
    description: description ?? "",
    contacts: [],
  });

  const setPortfolioIntro = (intro: PortfolioIntro) => {
    const newIntro = {
      ...intro,
      contacts: intro.contacts.map((contact) => ({ ...contact })),
    };
    setPortfolioLocalUpdateTime(new Date());
    setItem(newIntro);
  };

  const updateIntroName = (newName: string) => {
    const newPortfolioIntro = {
      ...portfolioIntro,
    };
    newPortfolioIntro.name = newName;

    setPortfolioIntro(newPortfolioIntro);
  };

  const updateIntroDescription = (newDescription: string) => {
    const newPortfolioIntro = {
      ...portfolioIntro,
    };
    newPortfolioIntro.description = newDescription;

    setPortfolioIntro(newPortfolioIntro);
  };

  const updateIsProfileShown = (isProfileShown: boolean) => {
    const newPortfolioIntro = {
      ...portfolioIntro,
    };
    newPortfolioIntro.isProfileShown = isProfileShown;

    setPortfolioIntro(newPortfolioIntro);
  };

  return {
    portfolioIntro,
    setPortfolioIntro,
    updateIntroName,
    updateIntroDescription,
    updateIsProfileShown,
  };
};

export default usePortfolioIntro;
